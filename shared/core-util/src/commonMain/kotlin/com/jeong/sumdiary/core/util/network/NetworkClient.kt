package com.jeong.sumdiary.core.util.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logging

object NetworkClient {
    val client: HttpClient by lazy {
        HttpClient {
            install(Logging)
        }
    }
}
