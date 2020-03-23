package com.itmo.ctd.design.labs.reactive.entity

import org.bson.Document

/**
 * @author mtereshchuk
 */
open class Goods(
        val id: Int,
        val name: String,
        val ruble: String,
        val dollar: String,
        val euro: String
) : DocumentEntity {
    override fun toDocument(): Document {
        return Document("id", id)
                .append("name", name)
                .append("ruble", ruble)
                .append("dollar", dollar)
                .append("euro", euro)
    }

    fun toString(currency: String): String {
        val price = when (currency) {
            "ruble" -> ruble
            "dollar" -> dollar
            "euro" -> euro
            else -> error("Unknown currency")
        }
        return "Goods(id=$id, name='$name', $currency='$price')"
    }

    companion object {
        fun fromDocument(document: Document): Goods {
            return Goods(
                    document.getInteger("id"),
                    document.getString("name"),
                    document.getString("ruble"),
                    document.getString("dollar"),
                    document.getString("euro")
            )
        }
    }
}