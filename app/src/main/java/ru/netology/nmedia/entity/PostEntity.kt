package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    var likedByMe: Boolean,
    val likes: Int = 0,
    var viewed: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?
) {
    fun toDto(): Post {
        return Post(
            id,
            authorId,
            author,
            authorAvatar,
            content,
            published,
            likedByMe,
            likes,
            viewed,
            attachment?.toDto()
        )
    }

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                viewed = true,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)


