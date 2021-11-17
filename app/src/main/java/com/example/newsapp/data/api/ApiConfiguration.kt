package com.example.newsapp.data.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*

object ApiConfiguration {

    private const val API_KEY = "115534a19ce84f9989aa7dff08415811"

    private const val TIME_OUT = 60_000

    fun getKtorHttpClient(): HttpClient = ktorHttpClient

    private val ktorHttpClient = HttpClient(Android){

        install(JsonFeature){
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })


            engine {
                connectTimeout = TIME_OUT
                socketTimeout = TIME_OUT
            }
        }

        install(Logging){
            logger = object : Logger{
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        install(ResponseObserver){
            onResponse {  response ->
                Log.d("Http status: ", "${response.status.value}")
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("X-Api-Key", API_KEY)
            host = "newsapi.org/v2"
            url {
                protocol = URLProtocol.HTTPS
            }
        }
    }
}