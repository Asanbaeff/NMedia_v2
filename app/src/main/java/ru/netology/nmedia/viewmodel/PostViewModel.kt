package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.netology.nmedia.adapter.PostWithAuthor
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.AppError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    authorId = 1,
    content = "",
    likedByMe = false,
    likes = 0,
    published = 0,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
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

    fun loadPosts() = viewModelScope.launch {
        try {
            _data.value = _data.value?.copy(loading = true, error = null)


            val postsDeferred = async { repository.getAll() }
            val authorsDeferred = async { repository.getAllAuthors() }
            val posts = postsDeferred.await()
            val authors = authorsDeferred.await()

            val authorsMap = authors.associateBy { it.id }

            val postsWithAuthors = posts.map { post ->
                PostWithAuthor(
                    post,
                    authorsMap[post.authorId] ?: Author(0, "Unknown", "netology.jpg")
                )
            }

            _data.postValue(
                FeedModel(
                    posts = posts,
                    postsWithAuthors = postsWithAuthors,
                    loading = false,
                    error = null,
                    empty = posts.isEmpty()
                )
            )
        } catch (e: AppError) {
            _data.postValue(_data.value?.copy(loading = false, error = e))
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let { post ->
            try {
                repository.save(post)
                _postCreated.postValue(Unit)
            } catch (e: AppError) {
                _data.postValue(_data.value?.copy(error = e))
            } finally {
                edited.value = empty
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        val oldData = _data.value
        val updatedList = oldData?.postsWithAuthors.orEmpty().map { item ->
            if (item.post.id == id) {
                item.copy(
                    post = item.post.copy(
                        likedByMe = !item.post.likedByMe,
                        likes = if (item.post.likedByMe) item.post.likes - 1 else item.post.likes + 1
                    )
                )
            } else {
                item
            }
        }
        _data.value = oldData?.copy(postsWithAuthors = updatedList)

        try {
            val post = oldData?.posts?.find { it.id == id } ?: return@launch
            if (post.likedByMe) repository.dislikeById(id) else repository.likeById(id)
        } catch (e: AppError) {
            _data.postValue(oldData?.copy(error = e))
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        val oldData = _data.value
        _data.value = oldData?.copy(
            postsWithAuthors = oldData.postsWithAuthors.filterNot { it.post.id == id }
        )
        try {
            repository.removeById(id)
        } catch (e: AppError) {
            _data.postValue(oldData?.copy(error = e))
        }
    }
}

