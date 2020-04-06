package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.TestClock
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author mtereshchuk
 */
class TurnstileTest {
    @Test
    fun `base test`() {
        val now = LocalDateTime.of(2020, 1, 1, 0, 0)
        val clock = TestClock(now)
        val eventStore = EventStore()
        val turnstile = Turnstile(eventStore, clock)
        val managerAdmin = ManagerAdmin(eventStore, clock)
        val johnId = 1
        managerAdmin.createAccount(johnId, "John")

        assertFalse { turnstile.canGoInside(johnId) }
        assertFalse { turnstile.canGoOutside(johnId) }

        managerAdmin.extendAccount(johnId, 1)
        clock.plusHours(10)
        assertTrue { turnstile.canGoInside(johnId) }

        turnstile.goInside(johnId)
        assertFalse { turnstile.canGoInside(johnId) }
        assertFails { turnstile.goInside(johnId) }
        assertTrue { turnstile.canGoOutside(johnId) }

        turnstile.goOutside(johnId)
        assertFalse { turnstile.canGoOutside(johnId) }
        assertFails { turnstile.goOutside(johnId) }

        clock.plusHours(15)
        assertFalse { turnstile.canGoInside(johnId) }
        assertFalse { turnstile.canGoOutside(johnId) }
        assertFails { turnstile.goInside(johnId) }
        assertFails { turnstile.goOutside(johnId) }
    }
}