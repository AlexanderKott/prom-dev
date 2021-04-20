package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post, callback: CommonByIDCallback)
    fun removeById(id: Long, callback: CommonByIDCallback)

    fun getAllAsync(callback: GetAllCallback)
    fun likeByIDAsync (id: Long, callback: CommonByIDCallback)
    fun dislikeByIDAsync (id: Long, callback: CommonByIDCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface CommonByIDCallback {
        fun onSuccess(posts: Post) {}
        fun onError(e: Exception) {}
    }
}
