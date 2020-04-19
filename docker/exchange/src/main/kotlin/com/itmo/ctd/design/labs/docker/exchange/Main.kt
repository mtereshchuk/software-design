package com.itmo.ctd.design.labs.docker.exchange

import com.itmo.ctd.design.labs.docker.exchange.dao.ExchangeDao
import com.itmo.ctd.design.labs.docker.exchange.servlet.ExchangeServlet
import io.reactivex.netty.protocol.http.server.HttpServer

/**
 * @author mtereshchuk
 */
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val exchangeDao = ExchangeDao()
            val exchangeServlet = ExchangeServlet(exchangeDao)
            HttpServer.newServer(8080)
                    .start { request, response -> exchangeServlet.doGet(request, response) }
                    .awaitShutdown()
        }
    }
}