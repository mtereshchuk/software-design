package com.itmo.ctd.design.labs.docker.user.entity

/**
 * @author mtereshchuk
 */
data class User(val id: Int, val name: String, var balance: Int, val shares: MutableMap<String, Int> = mutableMapOf())