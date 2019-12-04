package ostelco

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class GyordeServiceTest {

    @Test
    fun startStopServer() {
        val s = GyordeServer(9998)
        s.start()
        s.stop()
    }

    @Test
    fun roundtrip() {
        val c = GyordeClient("localhost", 9998)
        val s = GyordeServer(9998)
        s.start()
        c.checkDevice("a", "b")
        s.stop()
    }
}