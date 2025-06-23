package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerTest {

    @LocalServerPort
    private var port: Int = 0

    private val greetingFuture = CompletableFuture<Greeting>()

    @Test
    fun testGreeting() {
        val stompClient = WebSocketStompClient(StandardWebSocketClient())
        stompClient.messageConverter = MappingJackson2MessageConverter()

        val handler = object : StompSessionHandlerAdapter() {
            override fun getPayloadType(headers: StompHeaders): Type {
                return Greeting::class.java // 메시지 타입 명시
            }

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                if (payload is Greeting) {
                    greetingFuture.complete(payload)
                }
            }
        }

        val session: StompSession = stompClient.connectAsync(
            "ws://localhost:$port/gs-guide-websocket",
            handler
        ).get(3, TimeUnit.SECONDS)

        session.subscribe("/topic/greetings", handler)

        session.send("/app/hello", HelloMessage("Test"))

        val greeting = greetingFuture.get(3, TimeUnit.SECONDS) // 타임아웃 증가
        assertEquals("Hello, Test!", greeting.content)
    }
}
