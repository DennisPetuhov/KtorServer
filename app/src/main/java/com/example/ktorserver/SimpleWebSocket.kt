package com.example.ktorserver

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun startWebSocket() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true; ignoreUnknownKeys = true; prettyPrint = true
        })
    }
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 30.toDuration(DurationUnit.SECONDS)
        timeout = 60.toDuration(DurationUnit.SECONDS)
        masking = false
        maxFrameSize = Long.MAX_VALUE
    }
    routing {

        webSocket("/websocket/1") {
            sendSerialized(MyMessage("Hello, world i am WebSocket!"))
        }
        webSocket("/websocket") {
            val myMessage = receiveDeserialized<MyMessage>()
            println("A customer with id ${myMessage.message} is received by the server.")

            incoming.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        println("Received text frame: $text")
                    }
                    is Frame.Binary -> {
                        val data = frame.readBytes()
                        println("Received binary frame: ${data.size} bytes")
                    }
                    is Frame.Close -> {
                        val reason = frame.readReason()
                        println("Received close frame: $reason")
                    }
                    is Frame.Ping -> {
                        println("Received ping frame")
                    }
                    is Frame.Pong -> {
                        println("Received pong frame")
                    }
                }
            }
        }
    }
}