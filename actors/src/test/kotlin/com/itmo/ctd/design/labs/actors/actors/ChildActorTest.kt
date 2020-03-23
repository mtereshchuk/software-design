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
class ChildSearchActorTest {
    @Test
    fun `base test`() {
        object : TestKit(system) {
            init {
                val child = system.actorOf(Props.create(ChildActor::class.java, GoogleClient()))
                within(Duration.ofSeconds(1)) {
                    child.tell("query", ref)
                    expectMsgAnyClassOf<SearchResult>(SearchResult::class.java)
                }
            }
        }
    }

    companion object {
        private lateinit var system: ActorSystem

        @BeforeAll
        @JvmStatic
        fun setUp() {
            system = ActorSystem.create()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            TestKit.shutdownActorSystem(system)
        }
    }
}