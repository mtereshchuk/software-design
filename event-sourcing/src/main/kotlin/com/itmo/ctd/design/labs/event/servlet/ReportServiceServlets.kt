package com.itmo.ctd.design.labs.event.servlet

import com.itmo.ctd.design.labs.event.core.ReportService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author mtereshchuk
 */
class FrequencyServlet(private val reportService: ReportService) : QueryServlet() {
    override fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse) {
        printHtmlResponse(response, "Frequency - ${reportService.frequency()}")
    }
}

class WeekFrequencyServlet(private val reportService: ReportService) : QueryServlet() {
    override fun baseDoGet(request: HttpServletRequest, response: HttpServletResponse) {
        printHtmlResponse(response, "Week frequency - ${reportService.weekFrequency()}")
    }
}