package com.macnotes.model

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val title: String = "새 메모",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val fontSize: Int = 15,
    val fontFamily: String = "Pretendard",
    val textColor: String = "#1A1A2E"
)
