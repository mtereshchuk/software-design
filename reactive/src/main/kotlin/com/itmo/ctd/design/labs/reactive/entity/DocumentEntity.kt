package com.itmo.ctd.design.labs.reactive.entity

import org.bson.Document

/**
 * @author mtereshchuk
 */
interface DocumentEntity {
    fun toDocument(): Document
}