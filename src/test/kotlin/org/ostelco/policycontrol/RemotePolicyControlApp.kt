package org.ostelco.policycontrol

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.protobuf.ByteString
import gyorde.Gyorde
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import javax.validation.Valid

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
            environment.lifecycle().manage(rpsm);
        }
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) = RemotePolicyControlApp().run(*args)
    }
}

class RemotePolicyServiceMgr(val rpcConfig: RemotePolicyControlConfig) : Managed {

    lateinit private var imsiToClientMap: Map<String, DeviceCheckClient>
    lateinit private var clients: List<DeviceCheckClient>


    // TODO: Add an init function that validates the configuration
    //       ... only valid IMSIs, hostnnames and portnumbers, that IMSIs
    //       are not registred twice.

    override fun start() {
        val theMap = mutableMapOf<String, DeviceCheckClient>()
        val theClients = mutableListOf<DeviceCheckClient>()

        for (config in rpcConfig.policyServerConfigs) {
            val client = DeviceCheckClient(config.hostname, config.port)
            for (imsi in config.imsilist) {
                theMap[imsi] = client
                theClients.add(client)
            }
        }
        imsiToClientMap = theMap
        clients = theClients
    }

    override fun stop() {
        clients.forEach{it.shutdown()}
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