package com.itmo.ctd.design.labs.event.servlet

import com.itmo.ctd.design.labs.event.core.ManagerAdmin
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author mtereshchuk
 */
class CreateAccountServlet(private val managerAdmin: ManagerAdmin) : CommandServlet() {
    override fun baseDoGet(request: HttpServletRequest) {
        val id = request.getParameter("id")?.toInt() ?: error("Missing 'id'")
        val name = request.getParameter("name") ?: error("Missing 'name'")
        managerAdmin.createAccount(id, name)
    }
}

class ExtendAccountServlet(private val managerAdmin: ManagerAdmin) : CommandServlet() {
    override fun baseDoGet(request: HttpServletRequest) {
        val id = request.getParameter("id")?.toInt() ?: error("Missing 'id'")
        val days = request.getParameter("days")?.toLong() ?: error("Missing 'days'")
        managerAdmin.extendAccount(id, days)
    }
}

class GetInfoServlet(private val managerAdmin: ManagerAdmin) : QueryServlet() {
    override fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse) {
        val id = request.getParameter("id")?.toInt() ?: error("Missing 'id'")
        val info = managerAdmin.getInfo(id)
        printHtmlResponse(
                response,
                "${info.name}: created - ${info.created}, expiration - ${info.expiration}, last visit - ${info.lastVisit}"
        )
    }
}