package io.github.hkusu.kotlinlambdaapp

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

// List の String 化は普通に toJson() を使用すればよい
inline fun <reified T> String.fromJsonList(): List<T>? = try {
    val parameterizedType = Types.newParameterizedType(List::class.java, T::class.java)
    Module.moshi.adapter<List<T>>(parameterizedType).fromJson(this)
} catch (e: Exception) {
    null
}
