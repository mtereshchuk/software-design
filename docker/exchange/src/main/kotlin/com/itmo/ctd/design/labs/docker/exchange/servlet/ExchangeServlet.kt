package com.itmo.ctd.design.labs.docker.exchange.servlet

import com.itmo.ctd.design.labs.docker.exchange.dao.ExchangeDao
import com.itmo.ctd.design.labs.docker.exchange.entity.CompanyShares
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import rx.Observable

/**
 * @author mtereshchuk
 */
class ExchangeServlet(private val exchangeDao: ExchangeDao) {
    fun doGet(request: HttpServerRequest<ByteBuf>, response: HttpServerResponse<ByteBuf>): Observable<Void> {
        val command = request.decodedPath.substring(1)
        val queryParameters = request.queryParameters.mapValues { it.value[0] }
        val (message, status) = when (command) {
            "add_company" -> addCompany(queryParameters) to HttpResponseStatus.OK
            "add_shares" -> addShares(queryParameters) to HttpResponseStatus.OK
            "get_shares" -> getShares(queryParameters) to HttpResponseStatus.OK
            "buy_shares" -> buyShares(queryParameters) to HttpResponseStatus.OK
            "sell_shares" -> sellShares(queryParameters) to HttpResponseStatus.OK
            "change_price" -> changePrice(queryParameters) to HttpResponseStatus.OK
            else -> "Unknown command: $command" to HttpResponseStatus.BAD_REQUEST
        }
        response.status = status
        return response.writeString(message.toObservable())
    }

    fun addCompany(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name", "price", "count")) {
            val (name, price, count) = it
            exchangeDao.addCompany(CompanyShares(name, price.toInt(), count.toInt()))
        }
    }

    fun addShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name", "count")) {
            val (name, count) = it
            exchangeDao.addShares(name, count.toInt())
        }
    }

    fun getShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name")) {
            val name = it[0]
            val shares = exchangeDao.getShares(name)
            shares?.toString() ?: "Company not found for name '$name'"
        }
    }

    fun buyShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name", "count")) {
            val (name, count) = it
            exchangeDao.buyShares(name, count.toInt())
        }
    }

    fun sellShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name", "count")) {
            val (name, count) = it
            exchangeDao.sellShares(name, count.toInt())
        }
    }

    fun changePrice(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("name", "price")) {
            val (name, price) = it
            exchangeDao.sellShares(name, price.toInt())
        }
    }

    private fun performCommand(queryParameters: Map<String, String>,
                               parameterNames: List<String>,
                               commandAction: (List<String>) -> String): String {
        val (parameters, notFound) = parameterNames.asSequence()
                .map { it to queryParameters[it] }
                .partition { it.second != null }
        if (notFound.isNotEmpty()) {
            val notFoundNames = notFound.map { it.first }
            return "Not found: " + notFoundNames.joinToString()
        }
        val parameterValues = parameters.map { it.second!! }
        return commandAction(parameterValues)
    }

    private fun String.toObservable() = Observable.just(this)
}