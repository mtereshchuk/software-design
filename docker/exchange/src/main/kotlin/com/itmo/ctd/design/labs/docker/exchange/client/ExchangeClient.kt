package com.itmo.ctd.design.labs.docker.exchange.client

import com.itmo.ctd.design.labs.docker.exchange.entity.CompanyShares
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 * @author mtereshchuk
 */
class ExchangeClient(port: Int = 8080) {
    private val client = HttpClientBuilder.create()
            .setConnectionTimeToLive(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build();
    private val uriPrefix = "http://localhost:$port/"

    fun getShares(id: Int): CompanyShares {
        val uri = uriPrefix + "get_shares?id=$id"
        val rawObject = httpGetCall(uri)
        return CompanyShares.of(rawObject)
    }

    fun buyShares(id: Int, count: Int) {
        val uri = uriPrefix + "buy_shares?id=$id&count=&$count"
        httpGetCall(uri)
    }

    fun sellShares(id: Int, count: Int) {
        val uri = uriPrefix + "sell_shares?id=$id&count=&$count"
        httpGetCall(uri)
    }

    private fun httpGetCall(uri: String): String {
        val httpGet = HttpGet(URI(uri))
        client.execute(httpGet).use {
            return EntityUtils.toString(it.entity)
        }
    }

    companion object {
        private const val TIMEOUT_SECONDS = 10L
    }
}