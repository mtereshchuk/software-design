package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.TestClock
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author mtereshchuk
 */
class ReportServiceTest {
    @Test
    fun `base test`() {
        val now = LocalDateTime.of(2020, 1, 1, 0, 0)
        val clock = TestClock(now)
        val eventStore = EventStore()
        val turnstile = Turnstile(eventStore, clock)
        val managerAdmin = ManagerAdmin(eventStore, clock)
        val reportService = ReportService(eventStore, clock)
        
        assertEquals(0.0, reportService.frequency(), "No visits")
        assertEquals(emptyMap(), reportService.weekFrequency(), "No visits")
        
        val johnId = 1
        managerAdmin.createAccount(johnId, "John")
        managerAdmin.extendAccount(johnId, 30)
        for (i in 1..14) {
            clock.plusDays(1)
            turnstile.goInside(johnId)
            turnstile.goOutside(johnId)
        }
        
        val freq = reportService.weekFrequency()
        assertTrue { freq.values.any { it > 0.0 } }
        assertTrue { freq.all { it.value - 2.0 / 7.0 < 0.000001 } }
    }
}