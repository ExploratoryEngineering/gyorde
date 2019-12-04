package ostelco

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class GyordeServiceTest {


    @Test
    fun roundtrip() {

        var incomingIp = "c"
        var incomingImsi = "d"
        fun predicate(ipnr: String, imsi: String): Boolean {
            incomingImsi = imsi
            incomingIp = ipnr
            return true
        }

        val c = GyordeClient("localhost", 9998)
        val s = GyordeServer(9998, ::predicate)
        s.start()
        val x = c.checkDevice("a", "b")
        assertEquals("a", incomingImsi)
        assertEquals("b", incomingIp)
        s.stop()
    }
}