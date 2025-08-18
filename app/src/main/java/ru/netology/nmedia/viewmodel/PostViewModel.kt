package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let { post ->
            repository.saveAsync(post, object : PostRepository.SaveCallback {
                override fun onSuccess() {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(error = true))
                }
            })
        }
        edited.value = empty
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
        val oldPosts = _data.value?.posts.orEmpty()
        val postToUpdate = oldPosts.find { it.id == id } ?: return

        // Оптимистичное обновление
        val updatedPostOptimistic = postToUpdate.copy(
            likedByMe = !postToUpdate.likedByMe,
            likes = if (postToUpdate.likedByMe) postToUpdate.likes - 1 else postToUpdate.likes + 1
        )
        _data.postValue(
            _data.value?.copy(posts = oldPosts.map { if (it.id == id) updatedPostOptimistic else it })
        )

        repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
            override fun onSuccess(post: Post) {
                _data.postValue(
                    _data.value?.copy(posts = oldPosts.map { if (it.id == id) post else it })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = oldPosts, error = true))
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(_data.value?.copy(posts = old.filter { it.id != id }))

        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess() {}
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old, error = true))
            }
        })
    }
}
