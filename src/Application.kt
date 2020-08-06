package com.smat

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {

    val port = System.getenv("PORT")?.toInt() ?: 23567
    embeddedServer(Netty, port) {

    }
    val userController = UserController()
    fun initDB() {
        val config = HikariConfig("/hikari.properties")
        config.schema = "public"
        val ds = HikariDataSource(config)
        Database.connect(ds)
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    initDB()
    routing {

        get("/users") {
            call.respond(userController.getAll())
        }

        post("/users") {
            val userDto = call.receive<UserDTO>()
            userController.insert(userDto)
            call.respond( HttpStatusCode.Created,userDto)
        }

        put("/users/{id}") {
            val id: UUID = UUID.fromString(call.parameters["id"])
            val userDTO = call.receive<UserDTO>()
            userController.update(userDTO, id)
            call.respond(HttpStatusCode.OK)
        }

        delete("/users/{id}") {
            val id: UUID = UUID.fromString(call.parameters["id"])
            userController.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
