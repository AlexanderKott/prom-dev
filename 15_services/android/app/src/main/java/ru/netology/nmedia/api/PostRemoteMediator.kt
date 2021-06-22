package ru.netology.nmedia.api

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostKeyEntry
import ru.netology.nmedia.error.ApiError

@ExperimentalPagingApi
class PostRemoteMediator(private val api: ApiService,
                         private val base: AppDb)
    : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
            ): MediatorResult {
        try {
            val response = when (loadType){
                  LoadType.REFRESH -> {
                      base.postDao().deleteAll()
                      api.getLatest(state.config.pageSize)
                  }
                  LoadType.APPEND -> {
                      val id = base.keyWorkDao().min() ?: return MediatorResult.Success(false)
                      api.getBefore(id, state.config.pageSize)
                  }
                  LoadType.PREPEND -> {
                      val id = base.keyWorkDao().max() ?: return MediatorResult.Success(false)
                      api.getAfter(id, state.config.pageSize)
                  }
            }

           if (! response.isSuccessful){
               throw ApiError(response.code(), response.message())
           }

            val body = response.body() ?: throw  ApiError(
                response.code(),
                response.message()
            )

            if (body.isEmpty()){
                return MediatorResult.Success(true)
            }

            base.withTransaction {
                when (loadType){
                    LoadType.REFRESH -> {
                        base.keyWorkDao().insert(
                            listOf(
                                 PostKeyEntry(
                                     PostKeyEntry.Type.PREPEND,
                                     body.first().id
                                 ),
                                PostKeyEntry(
                                    PostKeyEntry.Type.APPEND,
                                    body.last().id
                                )
                            )
                        )
                        base.postDao().deleteAll()
                    }
                    LoadType.PREPEND -> {
                        base.keyWorkDao().insert(
                            listOf(
                                PostKeyEntry(
                                    PostKeyEntry.Type.PREPEND,
                                    body.first().id
                                ),
                            )
                        )

                    }
                    LoadType.APPEND ->  {
                        base.keyWorkDao().insert(
                            listOf(
                                PostKeyEntry(
                                    PostKeyEntry.Type.APPEND,
                                    body.last().id
                                )
                            )
                        )

                    }
                }

                base.postDao().insert(body.map(PostEntity.Companion::fromDto))
            }



            return  MediatorResult.Success(false)
        } catch (e: Exception){
           return MediatorResult.Error(e)
        }
    }
}