package com.itmo.ctd.design.labs.reactive

import com.itmo.ctd.design.labs.reactive.entity.Goods
import com.itmo.ctd.design.labs.reactive.entity.User
import com.mongodb.rx.client.Success
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.then
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import rx.Observable

/**
 * @author mtereshchuk
 */
class GoodsServletTest {
    @Mock
    private lateinit var driver: ReactiveMongoDriver
    @InjectMocks
    private lateinit var servlet: GoodsServlet

    @Mock
    private lateinit var request: HttpServerRequest<ByteBuf>
    @Mock
    private lateinit var response: HttpServerResponse<ByteBuf>

    @BeforeEach
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `add user`() {
        val command = "/addUser"
        val parameters = mapOf("id" to "1", "name" to "name", "currency" to "ruble")
                .mapValues { listOf(it.value) }

        given(request.decodedPath)
                .willReturn(command)
        given(request.queryParameters)
                .willReturn(parameters)
        given(driver.addUser(any()))
                .willReturn(Success.SUCCESS)

        servlet.doGet(request, response)

        assertEquals(HttpResponseStatus.OK, response.status)
        then(driver).should()
                .addUser(User(1, "name", "ruble"))
    }

    @Test
    fun `add goods`() {
        val command = "/addGoods"
        val parameters = mapOf("id" to "1", "name" to "name", "ruble" to "90", "dollar" to "1", "euro" to "1")
                .mapValues { listOf(it.value) }

        given(request.decodedPath)
                .willReturn(command)
        given(request.queryParameters)
                .willReturn(parameters)
        given(driver.addGoods(any()))
                .willReturn(Success.SUCCESS)

        servlet.doGet(request, response)

        assertEquals(HttpResponseStatus.OK, response.status)
        then(driver).should()
                .addGoods(Goods(1, "name", "90", "1", "1"))
    }

    @Test
    fun `get goods`() {
        val command = "/getGoods"
        val parameters = mapOf("id" to "1")
                .mapValues { listOf(it.value) }
        val observable = Observable.just(Goods(1, "name", "90", "1", "1").toString("ruble"))

        given(request.decodedPath)
                .willReturn(command)
        given(request.queryParameters)
                .willReturn(parameters)
        given(driver.getGoods(any()))
                .willReturn(observable)

        servlet.doGet(request, response)

        assertEquals(HttpResponseStatus.OK, response.status)
    }
}