package ru.netology.nmedia.viewmodel



import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
 
import ru.netology.nmedia.model.*
 
import ru.netology.nmedia.repository.BadConnectionException
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.coroutines.coroutineContext

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
    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    var internetErrorMessage = SingleLiveEvent<Boolean>()


    init {
        loadPosts()
    }


    fun loadPosts() {
 
        _data.value = LoadingFeed(true)
 

        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                if (posts.isEmpty()) {
                    _data.value = EmptyFeed(true)
                } else {
                    _data.value = PostsFeed(posts)
                }
            }

            override fun onError(e: Exception) {
                if (e is BadConnectionException) {
 
                    internetErrorMessage.value = true
                } else {
                    _data.value = ErrorFeed(true)
 
                }
            }
        })
    }


    fun save() {

        edited.value?.let {

            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
 
                    if (_data.value is PostsFeed) {
                        if (edited.value?.id != empty.id) {

                            _data.postValue(
                                PostsFeed(posts = (_data.value as PostsFeed)?.posts
                                    .orEmpty().map { if (it.id == post.id) post else it })
                            )
                        } else {

                            val temp: ArrayList<Post> = ArrayList((_data.value as PostsFeed)?.posts)
                            temp.add(post)
                            _data.postValue(PostsFeed(posts = temp))

                        }
                    }
 
                    _postCreated.value = Unit


                    Log.e("exec", "GOT save onSuccess")
                }

                override fun onError(e: Exception) {
                    if (e is BadConnectionException) {
 
                        internetErrorMessage.value = true
                        Log.e("exec", "GOT save internetError")
                    } else {
                        _data.value = ErrorFeed(error = true)
 
                        Log.e("exec", "GOT save error")
                    }

                    _postCreated.postValue(Unit)
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

 

    fun likeById(post: Post) {
        if (post.likedByMe) {
            repository.dislikeById(post.id, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    if (_data.value is PostsFeed) {
                        _data.postValue(
                            PostsFeed(posts = (_data.value as PostsFeed)?.posts
                                .orEmpty().map { if (it.id == post.id) post else it })
                        )
                    }
                }

                override fun onError(e: Exception) {
                    if (e is BadConnectionException) {
                        internetErrorMessage.value = true
                    } else {
                        _data.value = ErrorFeed(error = true)
                    }
                }

            })

        } else {

            repository.likeById(post.id, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    if (_data.value is PostsFeed) {
                        _data.postValue(
                            PostsFeed(posts = (_data.value as PostsFeed)?.posts
                                .orEmpty().map { if (it.id == post.id) post else it })
                        )
                    }
                }

                override fun onError(e: Exception) {
                    if (e is BadConnectionException) {
                        internetErrorMessage.value = true
                    } else {
                        _data.value = ErrorFeed(error = true)
                    }
                }

            })

        }
    }

    fun removeById(id: Long) {

        val old = if (_data.value is PostsFeed) {
            (_data.value as PostsFeed)?.posts.orEmpty()
        } else {
            EmptyFeed(true)
        }
 

        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(unit: Unit) {
                Log.e("exec", "GOT removeById onSuccess")
 
                if (_data.value is PostsFeed) {
                    _data.postValue(
                        (_data.value as PostsFeed)?.copy(posts = (_data.value as PostsFeed)?.posts.orEmpty()
                            .filter { it.id != id }
                        )
                    )
                }
 
            }

            override fun onError(e: Exception) {
                Log.e("exec", "GOT removeById onError")
                if (e is BadConnectionException) {
 
                    internetErrorMessage.value = true
                } else {
                    _data.value = ErrorFeed(error = true)
 
                }
            }
        })
    }
}
