package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class RefreshPostsWorker(
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val name = "ru.netology.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val repository: PostRepository =
            PostRepositoryImpl(
                AppDb.getInstance(context = applicationContext).postDao(),
                AppDb.getInstance(context = applicationContext).postWorkDao(),
            )

        try {
            repository.getAll()
            Result.success() //Воркер уведомляет тех кому это интрерсно (кто на него подписан)
                              // что результат успешен
                              //на воркер можно подписаться так же как и на лайв дату
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()   //Уведомить андроид ос, что метод надо повторить. Андроид сам выберет когда
        }
    }
}