package com.itmo.ctd.design.labs.event.core

import com.itmo.ctd.design.labs.event.event.Event

/**
 * @author mtereshchuk
 */
class EventStore {
    private val map = mutableMapOf<Int, MutableList<Event>>()
    private val subscribers = mutableListOf<(Int, Event) -> Unit>()

    operator fun get(key: Int) = map
            .getOrPut(key) { mutableListOf() }
            .let { it.subList(0, it.size) }
            .asIterable()

    operator fun set(key: Int, event: Event) {
        map.getOrPut(key) { mutableListOf() } += event
        subscribers.forEach { it(key, event) }
    }

    fun subscribe(subscriber: (Int, Event) -> Unit) {
        subscribers += subscriber
    }

    fun subscribeFromEpochStart(subscriber: (Int, Event) -> Unit) {
        map.forEach { (key, value) -> value.forEach { subscriber(key, it) } }
        subscribe(subscriber)
    }
}
