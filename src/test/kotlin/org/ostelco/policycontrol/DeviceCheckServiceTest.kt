package ostelco.org.ostelco.policycontrol

import com.google.protobuf.ByteString
import gyorde.Gyorde
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.ostelco.policycontrol.DeviceCheckClient
import org.ostelco.policycontrol.DeviceCheckServer
import org.ostelco.policycontrol.Predicate


class DeviceCheckServiceTest {

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

        assertEquals(true, x.success)
        assertEquals(outgoingImsi, incomingImsi)
        assertEquals(outgoingIpType, incomingIpType)
        assertEquals(outgoingIp, incomingIp)
    }
}