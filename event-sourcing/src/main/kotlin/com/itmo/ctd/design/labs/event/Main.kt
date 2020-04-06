package com.itmo.ctd.design.labs.event

import com.itmo.ctd.design.labs.event.core.EventStore
import com.itmo.ctd.design.labs.event.core.ManagerAdmin
import com.itmo.ctd.design.labs.event.core.ReportService
import com.itmo.ctd.design.labs.event.core.Turnstile
import com.itmo.ctd.design.labs.event.servlet.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.time.Clock

/**
 * @author mtereshchuk
 */
fun main() {
    val eventStore = EventStore()
    val clock = Clock.systemDefaultZone()
    val managerAdmin = ManagerAdmin(eventStore, clock)
    val turnstile = Turnstile(eventStore, clock)
    val reportService = ReportService(eventStore, clock)

    val server = Server(8081)
    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.contextPath = "/"
    server.handler = context

    context.addServlet(ServletHolder(CreateAccountServlet(managerAdmin)), "/create-account")
    context.addServlet(ServletHolder(ExtendAccountServlet(managerAdmin)), "/extend-account")
    context.addServlet(ServletHolder(GetInfoServlet(managerAdmin)), "/get-info")
    context.addServlet(ServletHolder(GoInsideServlet(turnstile)), "/go-inside")
    context.addServlet(ServletHolder(GoOutsideServlet(turnstile)), "/go-outside")
    context.addServlet(ServletHolder(FrequencyServlet(reportService)), "/get-frequency")
    context.addServlet(ServletHolder(WeekFrequencyServlet(reportService)), "/get-week-frequency")

    server.start()
    server.join()
}