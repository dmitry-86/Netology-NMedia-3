package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    var likedByMe: Boolean,
    var likes: Int = 0,
    var viewed: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE
}


