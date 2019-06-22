package io.github.hkusu.kotlinlambdaapp

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val main = LambdaHandler()

    // ローカルで実行する場合は
    // export API_TOKEN="your token"
    // で Slack 用の API トークンを環境変数を設定する(AWS Lambda として動かす場合は管理コンソールで設定)

    val jiraOutGoing = JiraOutGoing(
        webhookEvent = "jira:issue_updated"
    )

    println(main.handleRequest(LambdaInput(body = jiraOutGoing.toJson()), null))

    exitProcess(0) // Retrofitのsuspend関数を使うと何故かプロセスが終了しない為..
}
