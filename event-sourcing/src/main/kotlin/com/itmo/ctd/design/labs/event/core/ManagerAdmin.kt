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
import kotlin.random.Random

/**
 * @author mtereshchuk
 */
class ManagerAdmin(private val localStorage: LocalStorage, private val clock: Clock) {
    fun createAccount(personName: String): Int {
        val id = Random.nextInt(1000_000_000)
        localStorage[id] = AccountCreation(clock.localDate(), personName)
        return id
    }

    fun extendAccount(id: Int, days: Long) {
        localStorage[id] = AccountExtension(clock.localDate(), days)
    }

    fun getInfo(id: Int) = AccountInfo(localStorage, id)
}

class AccountInfo(localStorage: LocalStorage, id: Int) {
    var lastVisit: LocalDateTime? = null
        private set
    var expiration: LocalDate
        private set
    val created: LocalDate
    val name: String

    init {
        val events = localStorage[id]
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

