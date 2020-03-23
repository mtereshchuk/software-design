package com.itmo.ctd.design.labs.actors

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns.ask
import com.itmo.ctd.design.labs.actors.actors.MasterActor
import com.itmo.ctd.design.labs.actors.search.GoogleClient
import com.itmo.ctd.design.labs.actors.search.MailRuClient
import com.itmo.ctd.design.labs.actors.search.SearchResult
import com.itmo.ctd.design.labs.actors.search.YandexClient
import java.time.Duration

/**
 * @author mtereshchuk
 */
private val CLIENTS = listOf(GoogleClient(), YandexClient(), MailRuClient())
private const val TIMEOUT_MINUTES = 1L

fun main() {
    val system = ActorSystem.create()
    while (true) {
        println("Enter query:")
        val query = readLine() ?: return
        val master = system.actorOf(Props.create(MasterActor::class.java, CLIENTS))
        val result = ask(master, query, Duration.ofMinutes(TIMEOUT_MINUTES))
                .toCompletableFuture()
                .join() as List<SearchResult>
        println(result.joinToString(separator = "\n") { it.view() })
    }
}