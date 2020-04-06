package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.event.Event
import com.itmo.ctd.design.labs.event.event.UserCame
import com.itmo.ctd.design.labs.event.event.UserWentOut
import com.itmo.ctd.design.labs.event.event.get
import com.itmo.ctd.design.labs.event.utils.localDate
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @author mtereshchuk
 */
class ReportService(eventStore: EventStore, private val clock: Clock) {
    private val startTime: LocalDate = clock.localDate()
    private var totalDuration = 0L
    private var visits = 0
    private val map = mutableMapOf<Int, LocalDateTime>()
    private val weekInfo = mutableMapOf<DayOfWeek, Int>()

    init {
        eventStore.subscribeFromEpochStart { i, event -> eventHandler(i, event) }
    }

    fun frequency(): Double {
        return if (visits != 0) {
            val daysBetween = ChronoUnit.DAYS.between(startTime, clock.localDate()).toDouble()
            if (daysBetween > 0) visits.toDouble() else visits / daysBetween
        } else 0.0
    }

    fun weekFrequency(): Map<DayOfWeek, Double> {
        return if (visits != 0)
            weekInfo.mapValues { it.value.toDouble() / visits }
        else
            emptyMap()
    }

    private fun eventHandler(id: Int, event: Event) {
        when (event) {
            is UserCame -> {
                val day: LocalDateTime = event[UserCame.DATE_TIME]
                        ?: error("No date time inside")
                map[id] = day
                weekInfo[day.dayOfWeek] = weekInfo.getOrDefault(day.dayOfWeek, 0) + 1
            }
            is UserWentOut -> {
                val time = ChronoUnit.HOURS.between(
                        map[id],
                        event.get<LocalDateTime>(UserWentOut.DATE_TIME)
                                ?: error("No date time inside")
                )
                visits++
                totalDuration += time
            }
        }
    }
}