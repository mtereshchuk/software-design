package com.itmo.ctd.design.labs.docker.user.servlet;

import com.itmo.ctd.design.labs.docker.user.client.ExchangeClient;
import com.itmo.ctd.design.labs.docker.user.dao.UserDao;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author mtereshchuk
 */
public class UserServletIntegrationTest {
    @ClassRule
    public static final GenericContainer exchangeServer = new FixedHostPortGenericContainer("design-course-labs-docker-exchange:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);
    public static final UserServlet userServlet = new UserServlet(new UserDao(), new ExchangeClient());
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String uriPrefix = "http://localhost:8080/";

    @Test
    public void integrationTest() throws Exception {
        assertEquals("OK", sendRequest(uriPrefix + "add_company?name=google&price=1&count=10"));
        assertEquals("OK", userServlet.addUser(Map.of("id", "1", "name", "alex", "balance", "2")));
        assertEquals("OK", userServlet.buyShares(Map.of("id", "1", "company_name", "google", "count", "1")));
        assertEquals("company name = google, price = 1, count = 1", userServlet.getShares(Map.of("id", "1")));
        assertEquals("1", userServlet.getBalance(Map.of("id", "1")));
        assertEquals("2", userServlet.getFullBalance(Map.of("id", "1")));
        assertEquals("name = google, price = 1, count = 9", sendRequest(uriPrefix + "get_shares?name=google"));
    }

    private String sendRequest(final String uri) throws Exception {
        final var httpGet = HttpRequest.newBuilder(new URI(uri)).GET().build();
        return client.send(httpGet, HttpResponse.BodyHandlers.ofString()).body();
    }
}
