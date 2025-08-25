package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun getAllAuthors(): List<Author>
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
}
