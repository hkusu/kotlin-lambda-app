package io.github.hkusu.kotlinlambdaapp

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.lang.Exception

private const val BASE_URL = "https://slack.com:443"

private inline fun <reified T> createApi(okHttpClient: OkHttpClient, moshi: Moshi): T {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(T::class.java)
}

private fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

private fun createMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object Module {
    // 取得毎に新しいインスタンスを返す場合は get() = で定義する
    val moshi: Moshi = createMoshi()
    val chatApi: ChatApi = createApi(createOkHttpClient(), moshi)
}

inline fun <reified T> T.toJson(): String {
    return Module.moshi.adapter(T::class.java).toJson(this)
}

inline fun <reified T> String.fromJson(): T? = try {
    Module.moshi.adapter(T::class.java).fromJson(this)
} catch (e: Exception) {
    null
}

interface ChatApi {

    @POST("api/chat.postMessage")
    @Headers("Content-Type: application/json")
    suspend fun postChatMessage(
        @Header("Authorization") apiToken: String,
        @Body chatRequest: ChatRequest
    ): Response<ChatResponse>
}

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

data class LambdaInput(
    @Json(name = "hoge")
    val hoge: String,
    @Json(name = "fuga")
    val fuga: String,
    @Json(name = "piyo")
    val piyo: String
)

data class LambdaOutput(
    @Json(name = "statusCode")
    val statusCode: Int,
    @Json(name = "body")
    val body: String
)

data class Setting(
    @Json(name = "jira_id")
    val jiraId: String,
    @Json(name = "slack_id")
    val slackId: String
)

