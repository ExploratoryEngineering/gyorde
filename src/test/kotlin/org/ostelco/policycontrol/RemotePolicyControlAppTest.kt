package ostelco.org.ostelco.policycontrol

import com.google.protobuf.ByteString
import gyorde.Gyorde
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import io.dropwizard.testing.junit5.DropwizardAppExtension
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.policycontrol.DeviceCheckServer
import org.ostelco.policycontrol.Predicate
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
        val imsilist = mutableListOf("123456789012345", "123456789012344", "123456789012346")
        assertEquals(imsilist, firstServerConfig.imsilist)
    }

    @Test
    fun deviceCheckServicegRpcRoundtrip() {

        val predicate: Predicate =
            { imsi: Long,
              ipType: Gyorde.CheckDeviceRequest.IPType,
              ipAddress: ByteString ->
                true
            }

        val s = DeviceCheckServer(9998, predicate)

        s.start()
        val result = RULE.getApplication<RemotePolicyControlApp>().rpsm.checkPermissionFor("123456789012344""129.240.222.66")
        s.stop()

        assertEquals(true, result)
    }
}