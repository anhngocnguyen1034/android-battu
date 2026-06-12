package com.anhnn.battu.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body of POST /api/v1/feedback — forwarded by the backend to Discord. */
@Serializable
data class FeedbackRequestDto(
    val message: String,
    val rating: Int? = null,
    @SerialName("app_version") val appVersion: String = "",
    val device: String = "",
    @SerialName("os_version") val osVersion: String = "",
    val lang: String = ""
)
