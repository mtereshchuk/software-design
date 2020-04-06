package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.event.AccountCreation
import com.itmo.ctd.design.labs.event.event.AccountExtension
import com.itmo.ctd.design.labs.event.event.UserCame
import com.itmo.ctd.design.labs.event.event.get
import com.itmo.ctd.design.labs.event.utils.localDate
import com.itmo.ctd.design.labs.event.utils.nextExpirationDate
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author mtereshchuk
 */
class ManagerAdmin(private val eventStore: EventStore, private val clock: Clock) {
    fun createAccount(id: Int, name: String) {
        if (eventStore[id].firstOrNull() != null) {
            error("$id is busy")
        }
        eventStore[id] = AccountCreation(clock.localDate(), name)
    }

    fun extendAccount(id: Int, days: Long) {
        eventStore[id] = AccountExtension(clock.localDate(), days)
    }

    fun getInfo(id: Int) = AccountInfo(eventStore, id)
}

class AccountInfo(eventStore: EventStore, id: Int) {
    var lastVisit: LocalDateTime? = null
        private set
    var expiration: LocalDate
        private set
    val created: LocalDate
    val name: String

    init {
        val events = eventStore[id]
        if (events.first() !is AccountCreation) {
            error("Account wasn't created")
        }

        created = events.first()[AccountCreation.DATE] ?: error("Wrong date")
        name = events.first()[AccountCreation.NAME] ?: error("No name")
        expiration = created
        for (event in events.drop(1)) {
            when (event) {
                is AccountExtension -> expiration = nextExpirationDate(
                        event[AccountExtension.DATE] ?: error("No date"),
                        expiration,
                        event[AccountExtension.DAYS] ?: error("No days")
                )
                is UserCame -> lastVisit = event[UserCame.DATE_TIME]
                        ?: error("Wrong type")
            }
        }
    }
}

