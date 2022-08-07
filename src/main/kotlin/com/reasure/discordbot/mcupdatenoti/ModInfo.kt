package com.reasure.discordbot.mcupdatenoti

import com.google.gson.JsonArray

data class ModInfo(
    val id: Int = 0,
    val name: String = "",
    val summary: String = "",
    val link: String = "",
    val status: Int = 0,
    val responseCode: Response = Response.OK,
    val lastFiles: JsonArray? = null
)

enum class Response(code: Int) {
    OK(200), NOT_FOUND(404), INTERNAL_SERVER_ERROR(500), UNKNOWN(-1);
}