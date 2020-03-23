package com.itmo.ctd.design.labs.actors.actors

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import com.itmo.ctd.design.labs.actors.search.SearchClient
import com.itmo.ctd.design.labs.actors.search.SearchResult
import java.time.Duration

/**
 * @author mtereshchuk
 */
class MasterActor(private val clients: List<SearchClient>) : AbstractActor() {
    private lateinit var querySender: ActorRef
    private val results = mutableListOf<SearchResult>()

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(String::class.java) { query ->
                    querySender = sender

                    clients.forEach { client ->
                        context.actorOf(Props.create(ChildActor::class.java, client))
                                .tell(query, self)
                    }

                    context.system.scheduler.scheduleOnce(
                            Duration.ofSeconds(TIMEOUT_SECONDS),
                            self,
                            TimeoutMessage(),
                            context.system.dispatcher,
                            ActorRef.noSender()
                    )
                }
                .match(SearchResult::class.java) { result ->
                    results += result
                    if (results.size == clients.size) {
                        sendResultAndStopMessage()
                    }
                }
                .match(TimeoutMessage::class.java) { sendResultAndStopMessage() }
                .match(StopMessage::class.java) { context.stop(self) }
                .build()
    }

    private fun sendResultAndStopMessage() {
        querySender.tell(results, self)
        self.tell(StopMessage(), self)
    }

    private class TimeoutMessage
    private class StopMessage

    companion object {
        private const val TIMEOUT_SECONDS = 15L
    }
}