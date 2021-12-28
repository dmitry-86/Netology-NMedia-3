package ru.netology.nmedia.dto

import androidx.room.Embedded

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    var likes: Int = 0,
    var viewed: Boolean = false,
    val attachment: Attachment? = null

)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}


