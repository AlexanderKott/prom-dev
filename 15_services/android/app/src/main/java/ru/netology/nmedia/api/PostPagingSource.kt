package ru.netology.nmedia.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.dto.Post

class PostPagingSource(private val api: ApiService) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params){
                is LoadParams.Refresh -> api.getLatest(params.loadSize)
                is LoadParams.Append -> api.getBefore(params.key, params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    emptyList(),
                    params.key,
                    null
                )
            }

            val data  = result.body() ?: error("empty body")
            val new = data.lastOrNull()?.id

            return LoadResult.Page(
                        data = data,
                        prevKey = params.key,
                        nextKey = new
                    )


        } catch (e: Exception){
           return LoadResult.Error(e)
        }
    }
}