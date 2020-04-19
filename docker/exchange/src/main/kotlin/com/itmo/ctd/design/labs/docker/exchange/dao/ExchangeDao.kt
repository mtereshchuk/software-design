package com.itmo.ctd.design.labs.docker.exchange.dao

import com.itmo.ctd.design.labs.docker.exchange.entity.CompanyShares

/**
 * @author mtereshchuk
 */
class ExchangeDao {
    private val companyMap = mutableMapOf<String, CompanyShares>()

    fun addCompany(shares: CompanyShares): String {
        val name = shares.name
        if (name in companyMap)
            return "name '$name' already exists"
        companyMap[name] = shares
        return "OK"
    }

    fun addShares(name: String, count: Int): String {
        nameCountValidate(name, count)?.let { return it }
        companyMap[name]!!.count += count
        return "OK"
    }

    fun getShares(name: String): CompanyShares? {
        return companyMap[name]
    }

    fun buyShares(name: String, count: Int): String {
        nameCountValidate(name, count)?.let { return it }
        val shares = companyMap[name]!!
        if (shares.count < count)
            return "count is too large: there is only ${shares.count}"
        shares.count -= count
        return "OK"
    }

    fun sellShares(name: String, count: Int): String {
        nameCountValidate(name, count)?.let { return it }
        val shares = companyMap[name]!!
        shares.count += count
        return "OK"
    }

    fun changePrice(name: String, price: Int): String {
        if (name !in companyMap)
            return "name '$name' doesn't exist"
        if (price <= 0)
            return "price must be positive"
        val shares = companyMap[name]!!
        shares.price = price
        return "OK"
    }

    private fun nameCountValidate(name: String, count: Int): String? {
        if (name !in companyMap)
            return "name '$name' doesn't exist"
        if (count <= 0)
            return "count must be positive"
        return null
    }
}

