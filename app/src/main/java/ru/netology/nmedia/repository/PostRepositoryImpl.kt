package ru.netology.nmedia.repository


import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import java.io.IOException


sealed class AppError(val code: Int, message: String) : RuntimeException(message) {
    class ApiError(code: Int, message: String) : AppError(code, message)
    class NetworkError(message: String) : AppError(-1, message)
    class UnknownError(message: String) : AppError(-2, message)
}

class PostRepositoryImpl : PostRepository {

    override suspend fun getAll(): List<Post> {
        try {
            return PostsApi.retrofitService.getAll()
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAllAuthors(): List<Author> {
        try {
            return PostsApi.retrofitService.getAllAuthors()
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            return PostsApi.retrofitService.save(post)
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            PostsApi.retrofitService.removeById(id)
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }

    override suspend fun likeById(id: Long): Post {
        try {
            return PostsApi.retrofitService.likeById(id)
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }

    override suspend fun dislikeById(id: Long): Post {
        try {
            return PostsApi.retrofitService.dislikeById(id)
        } catch (e: IOException) {
            throw AppError.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            throw AppError.UnknownError(e.message ?: "Unknown error")
        }
    }


    private fun mapError(t: Throwable): AppError = when (t) {
        is IOException -> AppError.NetworkError(t.message ?: "Network error")
        else -> AppError.UnknownError(t.message ?: "Unknown error")
    }
}
