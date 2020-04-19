package com.itmo.ctd.design.labs.docker.exchange.entity

/**
 * @author mtereshchuk
 */
data class CompanyShares(val name: String, var price: Int, var count: Int) {
    override fun toString(): String {
        return "name = $name, price = $price, count = $count"
    }

    companion object {
        fun of(rawObject: String): CompanyShares {
            val (name, price, count) = rawObject.splitToSequence(",")
                    .map { it.split("=")[1] }
                    .map { it.trim() }
                    .toList()
            return CompanyShares(name, price.toInt(), count.toInt())
        }
    }
}