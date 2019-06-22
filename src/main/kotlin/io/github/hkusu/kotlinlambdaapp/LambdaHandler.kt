package io.github.hkusu.kotlinlambdaapp

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import kotlinx.coroutines.*
import java.lang.Exception
import java.io.FileInputStream

private const val ACCOUNT_SETTINGS_FILE_PATH = "data/account_settings.json"

class LambdaHandler: RequestHandler<LambdaInput, LambdaOutput> {

    override fun handleRequest(input: LambdaInput?, context: Context?): LambdaOutput {
        try {
            val apiToken: String = System.getenv("API_TOKEN") ?: throw IllegalArgumentException("API_TOKEN not found.")

            // println("body=${input?.body}")

            val accountSettingList: List<AccountSetting> =
                getAccountSettings() ?: throw IllegalArgumentException("Invalid JSON file.")

            val slackChannelList: List<String> = mutableListOf() // 通知先 Slack チャンネル

            runBlocking {
                coroutineScope {
                    listOf(
                        "#kusu_test_github",
                        "#kusu_test_github",
                        "#kusu_test_github"
                    ).map { channel ->
                        async {
                            // 並列でリクエスト
                            post2Slack(
                                channel = channel,
                                apiToken = apiToken
                            )
                        }
                    }.awaitAll()
                }
            }.let { resultList ->
                if (resultList.isEmpty()) {
                    return LambdaOutput(
                        statusCode = 200,
                        body = "Process has been passed through."
                    )
                }
                if (resultList.any { !it }) { // エラーが含まれる場合
                    return LambdaOutput(
                        statusCode = 500,
                        body = "Some were not posted."
                    )
                }
                return LambdaOutput(
                    statusCode = 200,
                    body = "All posts to slack."
                )
            }
        } catch (e: Exception) {
            return LambdaOutput(
                statusCode = 500,
                body = e.message ?: "Unknown error."
            )
        }
    }
}

fun getAccountSettings(): List<AccountSetting>? = try {

    val fileInputStream = FileInputStream(ACCOUNT_SETTINGS_FILE_PATH)
    val buffer = ByteArray(fileInputStream.available())
    fileInputStream.read(buffer)
    fileInputStream.close()

    String(buffer).fromJsonList()
} catch (e: Exception) {
    null
}

suspend fun post2Slack(channel: String, apiToken: String): Boolean {

    val chatApi: ChatApi = Module.chatApi

    val requestData = ChatApi.ChatRequest(
        userName = "hoge",
        iconUrl = "",
        channel = channel,
        attachments = listOf(
            ChatApi.ChatRequest.Attachments(
                title = "テステス",
                text = "ここはテキスト"
            )
        )
    )

    val response = chatApi.postChatMessage("Bearer $apiToken", requestData)

    // 他のAPIリクエストを実行させる為に例外をthrowしない
    if (!response.isSuccessful) return false
    val body = response.body() ?: return false
    if (!body.ok) return false

    return true
}
