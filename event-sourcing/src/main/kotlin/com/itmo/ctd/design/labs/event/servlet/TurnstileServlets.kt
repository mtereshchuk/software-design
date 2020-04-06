package com.itmo.ctd.design.labs.event.servlet

import com.itmo.ctd.design.labs.event.core.Turnstile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author mtereshchuk
 */
class GoInsideServlet(private val turnstile: Turnstile) : CommandServlet() {
    override fun baseDoGet(request: HttpServletRequest) {
        val id = request.getParameter("id")?.toInt() ?: error("Missing 'id'")
        turnstile.goInside(id)
    }
}

class GoOutsideServlet(private val turnstile: Turnstile) : CommandServlet() {
    override fun baseDoGet(request: HttpServletRequest) {
        val id = request.getParameter("id")?.toInt() ?: error("Missing 'id'")
        turnstile.goOutside(id)
    }
}