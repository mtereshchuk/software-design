package com.itmo.ctd.design.labs.docker.user.servlet

import com.itmo.ctd.design.labs.docker.user.client.ExchangeClient
import com.itmo.ctd.design.labs.docker.user.dao.UserDao
import com.itmo.ctd.design.labs.docker.user.entity.User
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import rx.Observable

/**
 * @author mtereshchuk
 */
class UserServlet(private val userDao: UserDao, private val exchangeClient: ExchangeClient) {
    fun doGet(request: HttpServerRequest<ByteBuf>, response: HttpServerResponse<ByteBuf>): Observable<Void> {
        val command = request.decodedPath.substring(1)
        val queryParameters = request.queryParameters.mapValues { it.value[0] }
        val (message, status) = when (command) {
            "add_user" -> addUser(queryParameters) to HttpResponseStatus.OK
            "add_money" -> addMoney(queryParameters) to HttpResponseStatus.OK
            "get_balance" -> getBalance(queryParameters) to HttpResponseStatus.OK
            "get_full_balance" -> getFullBalance(queryParameters) to HttpResponseStatus.OK
            "get_shares" -> getShares(queryParameters) to HttpResponseStatus.OK
            "buy_shares" -> buyShares(queryParameters) to HttpResponseStatus.OK
            "sell_shares" -> sellShares(queryParameters) to HttpResponseStatus.OK
            else -> "Unknown command: $command" to HttpResponseStatus.BAD_REQUEST
        }
        response.status = status
        return response.writeString(message.toObservable())
    }

    fun addUser(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id", "name", "balance")) {
            val (id, name, balance) = it
            userDao.addUser(User(id.toInt(), name, balance.toInt()))
        }
    }

    fun addMoney(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id", "money")) {
            val (id, money) = it
            userDao.addMoney(id.toInt(), money.toInt())
        }
    }

    fun getBalance(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id")) {
            val id = it[0]
            val balance = userDao.getBalance(id.toInt())
            balance?.toString() ?:"User not found for id '$id'"
        }
    }

    fun getFullBalance(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id")) {
            val id = it[0]
            val user = userDao.getUser(id.toInt())
                    ?: return@performCommand "User not found for id '$id'"
            var fullBalance = user.balance
            user.shares.forEach { (companyName, count) ->
                fullBalance += exchangeClient.getShares(companyName).price * count
            }
            fullBalance.toString()
        }
    }

    fun getShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id")) {
            val id = it[0].toInt()
            val user = userDao.getUser(id)
                    ?: return@performCommand "User not found for id '$id'"
            user.shares.map { (companyName, count) ->
                val price = exchangeClient.getShares(companyName).price
                "company name = $companyName, price = $price, count = $count"
            }.joinToString("\n")
        }
    }

    fun buyShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id", "company_name", "count")) {
            val (id, companyName, count) = it
            exchangeClient.buyShares(companyName, count.toInt())
            userDao.buyShares(id.toInt(), companyName, count.toInt())
        }
    }

    fun sellShares(queryParameters: Map<String, String>): String {
        return performCommand(queryParameters, listOf("id", "company_name", "count")) {
            val (id, companyName, count) = it
            exchangeClient.sellShares(companyName, count.toInt())
            userDao.sellShares(id.toInt(), companyName, count.toInt())
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