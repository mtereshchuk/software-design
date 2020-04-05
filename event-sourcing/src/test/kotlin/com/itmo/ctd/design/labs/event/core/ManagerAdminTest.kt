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
        val storage = LocalStorage()
        val turnstile = Turnstile(storage, clock)
        val managerAdmin = ManagerAdmin(storage, clock)

        val john = managerAdmin.createAccount("John")
        val jack = managerAdmin.createAccount("Jack")
        managerAdmin.extendAccount(john, 1)
        managerAdmin.extendAccount(jack, 30)

        clock.plusHours(10)
        turnstile.goInside(john)
        turnstile.goOutside(john)

        val johnInfo = managerAdmin.getInfo(john)
        val jackInfo = managerAdmin.getInfo(jack)

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