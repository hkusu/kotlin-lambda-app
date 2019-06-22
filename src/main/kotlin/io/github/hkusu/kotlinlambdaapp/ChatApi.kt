package io.github.hkusu.kotlinlambdaapp

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatApi {

    @POST("api/chat.postMessage")
    @Headers("Content-Type: application/json")
    suspend fun postChatMessage(
        @Header("Authorization") apiToken: String,
        @Body chatRequest: ChatRequest
    ): Response<ChatResponse>

    data class ChatRequest(
        @Json(name = "username")
        val userName: String,
        @Json(name = "icon_url")
        val iconUrl: String,
        @Json(name = "channel")
        val channel: String,
        @Json(name = "attachments")
        val attachments: List<Attachments>
    ) {
        data class Attachments(
            @Json(name = "title")
            val title: String,
            @Json(name = "text")
            val text: String
        )
    }

    data class ChatResponse(
        @Json(name = "ok")
        val ok: Boolean
    )
}
