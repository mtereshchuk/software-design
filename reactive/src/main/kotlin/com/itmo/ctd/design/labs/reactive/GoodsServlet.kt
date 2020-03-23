package com.itmo.ctd.design.labs.reactive

import com.itmo.ctd.design.labs.reactive.entity.Goods
import com.itmo.ctd.design.labs.reactive.entity.User
import com.mongodb.rx.client.Success
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import rx.Observable

/**
 * @author mtereshchuk
 */
open class GoodsServlet(private val driver: ReactiveMongoDriver) {
    fun doGet(request: HttpServerRequest<ByteBuf>, response: HttpServerResponse<ByteBuf>): Observable<Void> {
        val command = request.decodedPath.substring(1)
        val queryParameters = request.queryParameters.mapValues { it.value[0] }
        val (success, status) = when (command) {
            "addUser" -> addUser(queryParameters) to HttpResponseStatus.OK
            "addGoods" -> addGoods(queryParameters) to HttpResponseStatus.OK
            "getGoods" -> getGoods(queryParameters) to HttpResponseStatus.OK
            else -> Observable.just("Unknown command: $command") to HttpResponseStatus.BAD_REQUEST
        }
        response.status = status
        return response.writeString(success)
    }

    private fun addUser(queryParameters: Map<String, String>): Observable<String> {
        return performCommand(queryParameters, listOf("id", "name", "currency")) {
            val (id, name, currency) = it
            driver.addUser(User(id.toInt(), name, currency))
                    .toObservable()
        }
    }

    private fun addGoods(queryParameters: Map<String, String>): Observable<String> {
        return performCommand(queryParameters, listOf("id", "name", "ruble", "dollar", "euro")) {
            val (id, name, ruble, dollar, euro) = it
            driver.addGoods(Goods(id.toInt(), name, ruble, dollar, euro))
                    .toObservable()
        }
    }

    private fun getGoods(queryParameters: Map<String, String>): Observable<String> {
        return performCommand(queryParameters, listOf("id")) {
            val userId = it[0].toInt()
            val goods = driver.getGoods(userId)
            Observable.just("{ id = $userId, goods = [")
                    .concatWith(goods)
                    .concatWith(Observable.just("] }"))
        }
    }

    private fun performCommand(queryParameters: Map<String, String>,
                               parameterNames: List<String>,
                               commandAction: (List<String>) -> Observable<String>): Observable<String> {
        val (parameters, notFound) = parameterNames.asSequence()
                .map { it to queryParameters[it] }
                .partition { it.second != null }
        if (notFound.isNotEmpty()) {
            val notFoundNames = notFound.map { it.first }
            return Observable.just("Not found: " + notFoundNames.joinToString())
        }
        val parameterValues = parameters.map { it.second!! }
        return commandAction(parameterValues)
    }

    private fun Success.toObservable(): Observable<String> {
        return Observable.just(if (this == Success.SUCCESS) "Success" else "Failed add to DB")
    }
}