package com.itmo.ctd.design.labs.event.servlet;

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author mtereshchuk
 */
abstract class AbstractServlet : HttpServlet() {
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        baseDoGet(request, response);
        response.contentType = "text/html";
        response.status = HttpServletResponse.SC_OK;
    }

    fun printHtmlResponse(response: HttpServletResponse, content: String) {
        response.writer.use {
            it.println("<html><body>");
            it.println(content);
            it.println("</body></html>");
        }
    }

    protected abstract fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse)
}
