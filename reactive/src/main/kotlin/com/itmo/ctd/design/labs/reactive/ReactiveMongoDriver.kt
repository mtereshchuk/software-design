package com.itmo.ctd.design.labs.reactive

import com.itmo.ctd.design.labs.reactive.entity.DocumentEntity
import com.itmo.ctd.design.labs.reactive.entity.Goods
import com.itmo.ctd.design.labs.reactive.entity.User
import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * @author mtereshchuk
 */
open class ReactiveMongoDriver {
    fun addUser(user: User): Success {
        return add("user", user)
    }

    fun addGoods(goods: Goods): Success {
        return add("goods", goods)
    }

    fun getGoods(userId: Int): Observable<String> {
        return getCollection("user")
                .find(Filters.eq("id", userId))
                .first()
                .flatMap {
                    val currency = User.fromDocument(it).currency
                    getCollection("goods")
                            .find()
                            .toObservable()
                            .map { document ->
                                Goods.fromDocument(document).toString(currency)
                            }.reduce { a, b -> "$a, $b" }
                }
    }

    private fun add(name: String, entity: DocumentEntity): Success {
        return getCollection(name)
                .insertOne(entity.toDocument())
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .toBlocking()
                .single()
    }

    private fun getCollection(name: String) = client
            .getDatabase("rxtest")
            .getCollection(name)

    companion object {
        private const val TIMEOUT_SECONDS = 10L
        private val client = MongoClients.create()
    }
}
