package com.itmo.ctd.design.labs.docker.user

import com.itmo.ctd.design.labs.docker.user.client.ExchangeClient
import com.itmo.ctd.design.labs.docker.user.dao.UserDao
import com.itmo.ctd.design.labs.docker.user.servlet.UserServlet
import io.reactivex.netty.protocol.http.server.HttpServer

/**
 * @author mtereshchuk
 */
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val userDao = UserDao()
            val exchangeClient = ExchangeClient()
            val userServlet = UserServlet(userDao, exchangeClient)
            HttpServer.newServer(8081)
                    .start { request, response -> userServlet.doGet(request, response) }
                    .awaitShutdown()
        }
    }
}