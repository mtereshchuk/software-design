package com.itmo.ctd.design.labs.event.utils

import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @author mtereshchuk
 */
fun Clock.localDate(): LocalDate = LocalDate.ofInstant(instant(), zone)

fun Clock.localDateTime(): LocalDateTime = LocalDateTime.ofInstant(instant(), zone)

fun nextExpirationDate(date: LocalDate, expiration: LocalDate, days: Long): LocalDate {
    return date.plusDays(if (date >= expiration)
        days
    else
        ChronoUnit.DAYS.between(expiration, date) + days
    )
}