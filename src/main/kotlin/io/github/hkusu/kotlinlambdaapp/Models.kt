package io.github.hkusu.kotlinlambdaapp

import com.squareup.moshi.Json

data class LambdaInput(
    var body: String? = null // AWS側にデシリアライズを任す場合はvarである必要がある(setterが必要)
)

data class LambdaOutput(
    @Json(name = "statusCode")
    val statusCode: Int,
    @Json(name = "body")
    val body: String
)

data class JiraOutGoing(
    @Json(name = "webhookEvent")
    val webhookEvent: String,
    @Json(name = "issue_event_type_name")
    val issueEventYypeName: String? = null,
    @Json(name = "issue")
    val issue: Issue? = null,
    @Json(name = "comment")
    val comment: Comment? = null
) {
    data class Issue(
        @Json(name = "id")
        val id: String,
        @Json(name = "fields")
        val fields: String
    ) {
        data class Fields(
            @Json(name = "description")
            val description: String
        )
    }
    data class Comment(
        @Json(name = "id")
        val id: String,
        @Json(name = "body")
        val body: String,
        @Json(name = "updateAuthor")
        val updateAuthor: UpdateAuthor
    ) {
        data class UpdateAuthor(
            @Json(name = "name")
            val name: String,
            @Json(name = "avatarUrls")
            val avatarUrls: AvatarUrls,
            @Json(name = "AvatarUrls")
            val displayName: String
        ) {
            data class AvatarUrls(
                @Json(name = "16x16")
                val s16x16: String,
                @Json(name = "24x24")
                val s24x24: String,
                @Json(name = "32x32")
                val s32x32: String,
                @Json(name = "48x48")
                val s48x48: String
            )
        }
    }
}

data class AccountSetting(
    @Json(name = "jira_id")
    val jiraId: String,
    @Json(name = "slack_id")
    val slackId: String
)
