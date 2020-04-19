package com.itmo.ctd.design.labs.docker.user.client

import com.itmo.ctd.design.labs.docker.user.entity.CompanyShares
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * @author mtereshchuk
 */
class ExchangeClient(port: Int = 8080) {
    private val client = HttpClient.newHttpClient()
    private val uriPrefix = "http://localhost:$port/"

    fun getShares(companyName: String): CompanyShares {
        val uri = uriPrefix + "get_shares?name=$companyName"
        val rawObject = sendRequest(uri)
        return CompanyShares.of(rawObject)
    }

    fun buyShares(companyName: String, count: Int) {
        val uri = uriPrefix + "buy_shares?name=$companyName&count=&$count"
        sendRequest(uri)
    }

    fun sellShares(companyName: String, count: Int) {
        val uri = uriPrefix + "sell_shares?name=$companyName&count=&$count"
        sendRequest(uri)
    }

    private fun sendRequest(uri: String): String {
        val httpGet = HttpRequest.newBuilder(URI(uri)).GET().build()
        return client.send(httpGet, HttpResponse.BodyHandlers.ofString()).body()
    }
}