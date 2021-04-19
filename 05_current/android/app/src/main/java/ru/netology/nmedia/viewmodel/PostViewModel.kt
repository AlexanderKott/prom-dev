package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
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


    fun likeById(post: Post) {
        // old  thread { repository.likeById(id) }
        if (!post.likedByMe) {
            repository.likeByIDAsync(post.id, object : PostRepository.CommonByIDCallback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception) {
                }
            })
        } else {

            repository.dislikeByIDAsync(post.id, object : PostRepository.CommonByIDCallback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception) {
                }
            })
        }

    }


    fun save() {
        edited.value?.let {

                repository.save(it, object : PostRepository.CommonByIDCallback {
                    override fun onSuccess() {
                        Log.e("execx", "onSuccess")
                    }

                    override fun onError(e: Exception) {
                        Log.e("execx", "onError")
                    }
                })

                _postCreated.postValue(Unit)
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


    fun removeById(id: Long) {

            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id , object : PostRepository.CommonByIDCallback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(_data.value?.copy(posts = old))
                    }
                })
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }

    }
}
