package com.example.ktorserver


import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MyMessage(val message: String)

fun startServer() {
    embeddedServer(CIO, port = 8080) {
//        install(ContentNegotiation) {           json(Json { prettyPrint = true })     }

        install(ContentNegotiation) { json(Json { isLenient = true; ignoreUnknownKeys = true }) }
        routing {
            get("/api") {
                val message = MyMessage("Hello, world i am json!")
                call.respond(message)
            }
        }
    }.start(wait = true)
}
//fun startServer() {
//    embeddedServer(CIO, port = 8080) {
//
//        routing {
//            get("/api") {
//                call.respondText("Hello, world!")
////                call.respond(MyMessage("Hello, world MyMessage!"))
//            }
//        }
//    }.start(wait = true)
//}