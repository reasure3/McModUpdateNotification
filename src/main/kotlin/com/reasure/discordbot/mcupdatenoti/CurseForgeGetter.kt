package com.reasure.discordbot.mcupdatenoti

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CurseForgeGetter(private val key: String) {
    private val baseUrl = "https://api.curseforge.com"

    fun getModInfo(modId: Int): ModInfo {
        try {
            val url = URL("${baseUrl}/v1/mods/${modId}")
            val con = url.openGetConnection()
            if (con.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(con.inputStream))
                val data = reader.use(BufferedReader::readText)
                reader.close()
                con.disconnect()

                val json = JsonParser.parseString(data).asJsonObject.getAsJsonObject("data") as JsonObject
                return ModInfo(
                    id = modId,
                    name = json.get("name")?.asString ?: "Unknown",
                    summary = json.get("summary")?.asString ?: "",
                    status = json.get("status").asIntOrNull() ?: 0,
                    link = json.getAsJsonObject("links").get("websiteUrl")?.asString ?: "",
                    lastFiles = json.getAsJsonArray("latestFilesIndexes")
                )
            }

            con.disconnect()
            when (con.responseCode) {
                404 -> return ModInfo(responseCode = Response.NOT_FOUND, name = "Not Found")
                500 -> return ModInfo(responseCode = Response.INTERNAL_SERVER_ERROR, name = "Internal Server Error")
            }
        } catch (e: IOException) {
            println("Unknown Error")
        }
        return ModInfo(responseCode = Response.UNKNOWN, name = "Unknown")
    }

    private fun URL.openGetConnection(): HttpURLConnection {
        return (openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            setRequestProperty("x-api-key", key)
        }
    }
}