package com.itmo.ctd.design.labs.actors.actors

import akka.actor.AbstractActor
import com.itmo.ctd.design.labs.actors.search.SearchClient

/**
 * @author mtereshchuk
 */
class ChildActor(private val client: SearchClient) : AbstractActor() {
    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(String::class.java) { query -> sender.tell(client.search(query), self) }
                .build()
    }
}