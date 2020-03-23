package com.itmo.ctd.design.labs.reactive.entity

import org.bson.Document

/**
 * @author mtereshchuk
 */
open class User(
        val id: Int,
        val name: String,
        val currency: String
) : DocumentEntity {
    override fun toDocument(): Document {
        return Document("id", id)
                .append("name", name)
                .append("currency", currency)
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', currency='$currency')"
    }

    companion object {
        fun fromDocument(document: Document): User {
            return User(
                    document.getInteger("id"),
                    document.getString("name"),
                    document.getString("currency")
            )
        }
    }
}