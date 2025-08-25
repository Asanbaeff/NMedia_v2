package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachmentUrl: String? = null,
    val attachmentDescription: String? = null,
    val attachmentType: String? = null,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        attachment = if (attachmentUrl != null && attachmentType != null) {
            Attachment(
                url = attachmentUrl,
                description = attachmentDescription ?: "",
                type = AttachmentType.valueOf(attachmentType)
            )
        } else null
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            id = dto.id,
            authorId = dto.authorId,
            content = dto.content,
            published = dto.published,
            likedByMe = dto.likedByMe,
            likes = dto.likes,
            attachmentUrl = dto.attachment?.url,
            attachmentDescription = dto.attachment?.description,
            attachmentType = dto.attachment?.type?.name,
        )
    }
}

