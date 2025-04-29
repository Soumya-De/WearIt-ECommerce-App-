package com.example.ecommerceapp.domain.models

data class CommentModel(
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val user: String = "Anonymous",
    val userId: String = ""
)