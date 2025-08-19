package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.AppError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = _data.value?.copy(loading = true, error = null)

        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(
                    FeedModel(posts = result, loading = false, error = null)
                )
            }

            override fun onError(e: Throwable) {
                _data.postValue(
                    _data.value?.copy(loading = false, error = e as? AppError)
                )
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.save(post, object : PostRepository.Callback<Post> {
                override fun onSuccess(result: Post) {
                    val updated = _data.value?.posts.orEmpty()
                        .map { if (it.id == result.id) result else it }
                        .ifEmpty { listOf(result) }
                    _data.postValue(_data.value?.copy(posts = updated))
                    _postCreated.postValue(Unit)
                    edited.value = empty
                }

                override fun onError(e: Throwable) {
                    _data.postValue(_data.value?.copy(error = e as? AppError))
                }
            })
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        repository.likeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(result: Post) {
                val updated = _data.value?.posts.orEmpty().map {
                    if (it.id == id) result else it
                }
                _data.postValue(_data.value?.copy(posts = updated))
            }

            override fun onError(e: Throwable) {
                _data.postValue(_data.value?.copy(error = e as? AppError))
            }
        })
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(result: Unit) {
                val updated = _data.value?.posts.orEmpty().filterNot { it.id == id }
                _data.postValue(_data.value?.copy(posts = updated))
            }

            override fun onError(e: Throwable) {
                _data.postValue(_data.value?.copy(error = e as? AppError))
            }
        })
    }
}
