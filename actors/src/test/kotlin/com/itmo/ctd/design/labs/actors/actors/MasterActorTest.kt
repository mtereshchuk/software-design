package com.itmo.ctd.design.labs.actors.actors

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.javadsl.TestKit
import com.itmo.ctd.design.labs.actors.search.GoogleClient
import com.itmo.ctd.design.labs.actors.search.SearchResult
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.Duration

/**
 * @author mtereshchuk
 */
class MasterSearchActorTest {
    @Test
    fun `base test`() {
        object : TestKit(system) {
            init {
                val master = system.actorOf(Props.create(MasterActor::class.java, listOf(GoogleClient())))
                within(Duration.ofSeconds(1)) {
                    master.tell("query", ref)
                    expectMsgAnyClassOf<List<SearchResult>>(List::class.java)
                }
            }
        }
    }

    companion object {
        private lateinit var system: ActorSystem

        @BeforeAll
        @JvmStatic
        fun setup() {
            system = ActorSystem.create()
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            TestKit.shutdownActorSystem(system)
        }
    }
}