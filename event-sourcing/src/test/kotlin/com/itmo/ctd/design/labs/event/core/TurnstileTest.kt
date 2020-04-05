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
        val storage = LocalStorage()
        val turnstile = Turnstile(storage, clock)
        val managerAdmin = ManagerAdmin(storage, clock)
        val id = managerAdmin.createAccount("John")

        assertFalse { turnstile.canGoInside(id) }
        assertFalse { turnstile.canGoOutside(id) }

        managerAdmin.extendAccount(id, 1)
        clock.plusHours(10)
        assertTrue { turnstile.canGoInside(id) }

        turnstile.goInside(id)
        assertFalse { turnstile.canGoInside(id) }
        assertFails { turnstile.goInside(id) }
        assertTrue { turnstile.canGoOutside(id) }

        turnstile.goOutside(id)
        assertFalse { turnstile.canGoOutside(id) }
        assertFails { turnstile.goOutside(id) }

        clock.plusHours(15)
        assertFalse { turnstile.canGoInside(id) }
        assertFalse { turnstile.canGoOutside(id) }
        assertFails { turnstile.goInside(id) }
        assertFails { turnstile.goOutside(id) }
    }
}