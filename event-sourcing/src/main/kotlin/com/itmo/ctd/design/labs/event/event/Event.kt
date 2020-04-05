package com.itmo.ctd.design.labs.event.event

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author mtereshchuk
 */
interface Event {
    val attributes: Map<String, Any>
}

inline operator fun <reified T> Event.get(attr: String): T? {
    if (attributes[attr] is T?) {
        return attributes[attr] as T?
    }
    return null
}

class AccountCreation(date: LocalDate, name: String) : Event {
    override val attributes = mapOf(DATE to date, NAME to name)
    companion object {
        const val DATE = "date"
        const val NAME = "name"
    }
}

class AccountExtension(date: LocalDate, days: Long) : Event {
    override val attributes = mapOf(DATE to date, DAYS to days)
    companion object {
        const val DATE = "date"
        const val DAYS = "days"
    }
}

class UserCame(date: LocalDateTime) : Event {
    override val attributes = mapOf(DATE_TIME to date)
    companion object {
        const val DATE_TIME = "dateTime"
    }
}

class UserWentOut(date: LocalDateTime) : Event {
    override val attributes = mapOf(DATE_TIME to date)
    companion object {
        const val DATE_TIME = "dateTime"
    }
}