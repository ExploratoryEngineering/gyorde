package ostelco.org.ostelco.policycontrol

import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import io.dropwizard.testing.junit5.DropwizardAppExtension
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.policycontrol.RemotePolicyControlApp
import org.ostelco.policycontrol.RemotePolicyControlConfig


class RemotePolicyControlAppTest {

    // XXX TODO:
    // 1. Make this companion object work
    // 2. Make some weird roundtrip test
    // 3. Rework the policy control client into a lifecycle object
    // 4. That lifecycle object should have a healthcheck (we need a ping on the protocol)
    // 5. Shape this app so that it can accomodate that lifecycle object, but bear in mind that
    //    that in the end it will run somewhere else.
    companion object {

        @JvmField
        @ClassRule
        public val RULE = DropwizardAppRule<RemotePolicyControlConfig>(
            RemotePolicyControlApp::class.java,
            ResourceHelpers.resourceFilePath("remote-policy-control.yaml")
        )
    }

    @Test
    fun testConfigReading(){
        var policyServerConfigs = RULE.configuration.policyServerConfigs
        assertNotNull(policyServerConfigs)
        assertEquals(1, policyServerConfigs.size)
        var firstServerConfig = policyServerConfigs[0]
        assertEquals("test-server", firstServerConfig.name)
        assertEquals("127.0.0.1", firstServerConfig.hostname)
        assertEquals(9998, firstServerConfig.port)
    }
/*
    @Test
    fun deviceCheckServicegRpcRoundtrip() {

        // Hand-coding 129.240.222.66 into byte array.
        val bytes = byteArrayOf(129.toByte(), 240.toByte(), 222.toByte(), 66.toByte())
        val outgoingIp = ByteString.copyFrom(bytes)
        val outgoingImsi = 123456789012345
        val outgoingIpType = Gyorde.CheckDeviceRequest.IPType.IPV4

        var incomingIp = ByteString.copyFrom(byteArrayOf(10.toByte(), 10.toByte(), 10.toByte(), 10.toByte()))
        var incomingImsi = 999999999999999
        var incomingIpType = Gyorde.CheckDeviceRequest.IPType.IPV6

        val predicate: Predicate =
            { imsi: Long,
              ipType: Gyorde.CheckDeviceRequest.IPType,
              ipAddress: ByteString ->
                incomingImsi = imsi
                incomingIp = ipAddress
                incomingIpType = ipType

                true
            }

        val c = DeviceCheckClient("localhost", 9998)
        val s = DeviceCheckServer(9998, predicate)
        s.start()
        val x =
            c.checkDevice(outgoingImsi, outgoingIpType, outgoingIp)
        s.stop()

        Assertions.assertEquals(true, x.success)
        Assertions.assertEquals(outgoingImsi, incomingImsi)
        Assertions.assertEquals(outgoingIpType, incomingIpType)
        Assertions.assertEquals(outgoingIp, incomingIp)
    } */
}