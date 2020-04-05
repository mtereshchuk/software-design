package com.itmo.ctd.design.labs.event

import java.time.*

/**
 * @author mtereshchuk
 */
class TestClock(private var now: Instant) : Clock() {
    constructor(dateTime: LocalDateTime)
            : this(dateTime.atZone(ZoneId.systemDefault()).toInstant())

    override fun withZone(zone: ZoneId) = throw UnsupportedOperationException()
    override fun getZone(): ZoneId = ZoneId.systemDefault()
    override fun instant() = now

    fun plusHours(hours: Int) {
        now = now.plusSeconds(hours * 3600L)
    }

    fun plusDays(days: Int) {
        plusHours(days * 24)
    }
}