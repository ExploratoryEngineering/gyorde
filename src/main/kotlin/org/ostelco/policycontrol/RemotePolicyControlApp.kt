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

    override fun run(
        configuration: RemotePolicyControlConfig?,
        environment: Environment
    ) {
       //  environment.admin().addTask(MyTestTask())
       //  environment.jersey().register(resource)
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) = RemotePolicyControlApp().run(*args)
    }
}

/*
class RemotePolicService() : Managed {

    val blockingClient = DeviceCheckClient("localhost", 9998)

    fun ping() {
        val bytes = byteArrayOf(129.toByte(), 240.toByte(), 222.toByte(), 66.toByte())
        val outgoingIp = ByteString.copyFrom(bytes)
        val outgoingImsi = 123456789012345
        val outgoingIpType = Gyorde.CheckDeviceRequest.IPType.IPV4

        blockingClient.checkDevice(outgoingImsi, outgoingIpType, outgoingIp)
    }

}
*/

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