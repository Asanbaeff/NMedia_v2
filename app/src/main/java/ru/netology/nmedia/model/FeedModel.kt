package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.AppError

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: AppError? = null,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)
