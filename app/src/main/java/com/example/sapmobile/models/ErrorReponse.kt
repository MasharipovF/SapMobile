package com.example.sapmobile.models


data class ErrorResponse(
    var error: Error
)

data class Error(
    val code: Int,
    val message: Message
)

data class Message(
    val lang: String,
    val value: String
)
