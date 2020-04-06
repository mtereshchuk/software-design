package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.event.*
import com.itmo.ctd.design.labs.event.utils.localDate
import com.itmo.ctd.design.labs.event.utils.localDateTime
import com.itmo.ctd.design.labs.event.utils.nextExpirationDate
import java.time.Clock
import java.time.LocalDate

/**
 * @author mtereshchuk
 */
class Turnstile(private val eventStore: EventStore, private val clock: Clock) {
    fun goInside(id: Int) {
        require(canGoInside(id)) { "Can't go inside" }
        eventStore[id] = UserCame(clock.localDateTime())
    }

    fun goOutside(id: Int) {
        require(canGoOutside(id)) { "Can't go outside" }
        eventStore[id] = UserWentOut(clock.localDateTime())
    }

    fun canGoInside(id: Int): Boolean {
        val info = collectInfo(id)
        return clock.localDate() < info.first && info.second
    }

    fun canGoOutside(id: Int): Boolean {
        val info = collectInfo(id)
        return clock.localDate() < info.first && !info.second
    }

    private fun collectInfo(id: Int): Pair<LocalDate, Boolean> {
        val events = eventStore[id]
        if (events.first() !is AccountCreation) {
            error("Account wasn't created")
        }

        var expiration: LocalDate = events.first()[AccountCreation.DATE] ?: error("Wrong date")
        var last = true
        for (event in events.drop(1)) {
            when (event) {
                is AccountExtension -> expiration = nextExpirationDate(
                        event[AccountExtension.DATE] ?: error("No date"),
                        expiration,
                        event[AccountExtension.DAYS] ?: error("No days")
                )
                is UserCame -> last = false
                is UserWentOut -> last = true
            }
        }
        return expiration to last
    }
}
