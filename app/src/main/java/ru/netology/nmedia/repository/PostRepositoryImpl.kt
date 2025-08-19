package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException


sealed class AppError(val code: Int, message: String) : RuntimeException(message) {
    class ApiError(code: Int, message: String) : AppError(code, message)
    class NetworkError(message: String) : AppError(-1, message)
    class UnknownError(message: String) : AppError(-2, message)
}

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(AppError.ApiError(response.code(), response.message()))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        callback.onError(AppError.UnknownError("Response body is null"))
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(mapError(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(AppError.ApiError(response.code(), response.message()))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        callback.onError(AppError.UnknownError("Response body is null"))
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(mapError(t))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(AppError.ApiError(response.code(), response.message()))
                        return
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(mapError(t))
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(AppError.ApiError(response.code(), response.message()))
                        return
                    }
                    val body = response.body()
                    if (body == null) {
                        callback.onError(AppError.UnknownError("Response body is null"))
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(mapError(t))
                }
            })
    }


    private fun mapError(t: Throwable): AppError = when (t) {
        is IOException -> AppError.NetworkError(t.message ?: "Network error")
        else -> AppError.UnknownError(t.message ?: "Unknown error")
    }
}
