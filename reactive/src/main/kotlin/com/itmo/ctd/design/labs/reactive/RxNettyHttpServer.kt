package com.itmo.ctd.design.labs.reactive

import io.reactivex.netty.protocol.http.server.HttpServer

/**
 * @author mtereshchuk
 */
fun main() {
    val goodsServlet = GoodsServlet(ReactiveMongoDriver())
    HttpServer.newServer(8080)
            .start { request, response -> goodsServlet.doGet(request, response) }
            .awaitShutdown()
}