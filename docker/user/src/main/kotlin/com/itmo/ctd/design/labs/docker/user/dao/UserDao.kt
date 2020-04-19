package com.itmo.ctd.design.labs.docker.user.dao

import com.itmo.ctd.design.labs.docker.user.entity.User

/**
 * @author mtereshchuk
 */
class UserDao {
    private val userMap = mutableMapOf<Int, User>()

    fun addUser(user: User): String {
        val id = user.id
        if (id in userMap)
            return "id '$id' already exists"
        userMap[id] = user
        return "OK"
    }

    fun addMoney(id: Int, money: Int): String {
        if (id !in userMap)
            return "id '$id' doesn't exist"
        if (money <= 0)
            return "money must be positive"
        userMap[id]!!.balance += money
        return "OK"
    }

    fun getUser(id: Int): User? {
        return userMap[id]
    }

    fun getBalance(id: Int): Int? {
        return getUser(id)?.balance
    }

    fun buyShares(id: Int, companyName: String, count: Int): String {
        idCountValidate(id, count)?.let { return it }
        val user = getUser(id)!!
        val oldCount = user.shares[companyName] ?: 0
        user.shares[companyName] = oldCount + count
        return "OK"
    }

    fun sellShares(id: Int, companyName: String, count: Int): String {
        idCountValidate(id, count)?.let { return it }
        val user = getUser(id)!!
        val oldCount = user.shares[companyName] ?: 0
        if (oldCount < count) {
            return "count must not be greater than $oldCount"
        }
        user.shares[companyName] = oldCount - count
        return "OK"
    }

    private fun idCountValidate(id: Int, money: Int): String? {
        if (id !in userMap)
            return "id '$id' doesn't exist"
        if (money <= 0)
            return "money must be positive"
        return null
    }
}

