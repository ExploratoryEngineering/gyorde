package org.ostelco.policycontrol

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.protobuf.ByteString
import gyorde.Gyorde
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.log4j.spi.LoggerFactory
import java.util.*
import javax.servlet.DispatcherType
import javax.validation.Valid
import kotlin.collections.HashMap

/**
 * A module that will contact a remote policy control service to get authorization
 * for access by a sim profile identifiedd with an IMSI, and an assigned IP number.
 */
class RemotePolicyControlApp() : Application<RemotePolicyControlConfig?>() {

    override fun initialize(bootstrap: Bootstrap<RemotePolicyControlConfig?>) {
    }

    lateinit var rpsm: RemotePolicyServiceMgr

    override fun run(
        configuration: RemotePolicyControlConfig?,
        environment: Environment
    ) {
        if (configuration != null) {
            rpsm = RemotePolicyServiceMgr(configuration)
        }
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) = RemotePolicyControlApp().run(*args)
    }
}

class RemotePolicyServiceMgr(rpcConfig: RemotePolicyControlConfig) {

    private val imsiToClientMap: Map<String, DeviceCheckClient>

    init {
        val theMap = mutableMapOf<String, DeviceCheckClient>()

        for (config in rpcConfig.policyServerConfigs) {
            // TODO: Validate hostname/port values before using in client
            val client = DeviceCheckClient(config.hostname, config.port)
            for (imsi in config.imsilist) {
                // TODO: Validate imsi before using it as key
                // TODO: Check that imsi isn't already registred.
                theMap[imsi] = client
            }
        }
        imsiToClientMap = theMap
    }

    private fun getClientFor(imsi: String): DeviceCheckClient? {
        return imsiToClientMap[imsi]
    }

    private fun stringAsLong(imsi: String): Long {
        return imsi.toLong()
    }

    private fun ipAsByteString(ip: String): ByteString {
        val octetStrings = ip.split(".")
        val bytes: ByteArray = ByteArray(octetStrings.size) { pos -> octetStrings[pos].toInt().toByte() }
        return ByteString.copyFrom(bytes)
    }

    // This is the plain vanilla version. Later versions will be more
    // elaborate
    public fun checkPermissionFor(imsi: String, ip: String): Boolean {
        val client = imsiToClientMap[imsi]
        if (client == null) {
            return true
        } else {
            try {
                val returnValue =
                    client.checkDevice(stringAsLong(imsi), Gyorde.CheckDeviceRequest.IPType.IPV4, ipAsByteString(ip))
                // TODO: Some checking of error message etc.
                return returnValue.success
            } catch (e: Throwable) {
                // TODO: Logging
                println("Threw $e")
                return false
            }
        }
    }
}


class PolicyServerConfig {
    @Valid
    @JsonProperty("name")
    lateinit var name: String

    @Valid
    @JsonProperty("hostname")
    lateinit var hostname: String

    @Valid
    @JsonProperty("port")
    var port: Int = 0

    @Valid
    @JsonProperty("imsilist")
    lateinit var imsilist: List<String>
}

class RemotePolicyControlConfig constructor() : Configuration() {
    @Valid
    @JsonProperty("remote-policy-servers")
    lateinit var policyServerConfigs: List<PolicyServerConfig>
}