package ru.netology.nmedia.api

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsApi @Inject constructor(
    retrofit: Retrofit
) {
    val service: ApiService = retrofit.create(ApiService::class.java)
}
