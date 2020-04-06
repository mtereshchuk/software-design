package com.itmo.ctd.design.labs.event.servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author mtereshchuk
 */
sealed class AbstractServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        baseDoGet(request, response);
        response.contentType = "text/html";
        response.status = HttpServletResponse.SC_OK;
    }

    protected abstract fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse)
}

abstract class CommandServlet : AbstractServlet() {
    override fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse) {
        baseDoGet(request)
    }

    protected abstract fun baseDoGet(request: HttpServletRequest)
}

abstract class QueryServlet : AbstractServlet() {
    fun printHtmlResponse(response: HttpServletResponse, content: String) {
        response.writer.use {
            it.println("<html><body>");
            it.println(content);
            it.println("</body></html>");
        }
    }
}
