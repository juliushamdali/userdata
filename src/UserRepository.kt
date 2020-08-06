package com.smat

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = uuid("id").primaryKey()
    val firstName = text("firstName")
    val lastName = text("lastName")
    val age = integer("age")
}