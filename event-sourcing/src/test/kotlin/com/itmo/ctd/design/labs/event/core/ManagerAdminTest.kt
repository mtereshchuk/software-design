package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.TestClock
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.*

/**
 * @author mtereshchuk
 */
class ManagerAdminTest {
    @Test
    fun `base test`() {
        val now = LocalDateTime.of(2020, 1, 1, 0, 0)
        val clock = TestClock(now)
        val eventStore = EventStore()
        val turnstile = Turnstile(eventStore, clock)
        val managerAdmin = ManagerAdmin(eventStore, clock)

        val johnId = 1
        val jackId = 2
        managerAdmin.createAccount(johnId, "John")
        managerAdmin.createAccount(jackId, "Jack")
        managerAdmin.extendAccount(johnId, 1)
        managerAdmin.extendAccount(jackId, 30)

        clock.plusHours(10)
        turnstile.goInside(johnId)
        turnstile.goOutside(johnId)

        val johnInfo = managerAdmin.getInfo(johnId)
        val jackInfo = managerAdmin.getInfo(jackId)

        assertEquals(johnInfo.name, "John", "Wrong name")
        assertEquals(jackInfo.name, "Jack", "Wrong name")

        assertEquals(johnInfo.created, now.toLocalDate(), "Wrong created clock")
        assertEquals(johnInfo.created, now.toLocalDate(), "Wrong created clock")

        assertEquals(johnInfo.expiration, now.toLocalDate().plusDays(1), "Wrong expire date")
        assertEquals(jackInfo.expiration, now.toLocalDate().plusDays(30), "Wrong expire date")

        assertEquals(johnInfo.lastVisit, now.plusHours(10), "Wrong last visit time")
        assertEquals(jackInfo.lastVisit, null, "There must be no visits")
    }
}