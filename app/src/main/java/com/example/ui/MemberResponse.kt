package com.example.ui

data class MemberResponse(
    val id: Int,
    val whatsappNumber: String,
    val name: String
)

data class MemberRequest(
    val whatsappNumber: String,
    val name: String
)

