package ru.netology.nmedia.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

/**
 * Это переодичный воркер, андроид ос сам его заускает когда приходит время
 * выставленное при его инициализации
 */
class SavePostWorker(
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "post"
    }

    ///Это класс воркера, овверайдим doWork  который выполняет рабоу в фоне
    override suspend fun doWork(): Result {
        //Получаем входные данные (могут быть тольк оримитивы и стринг)
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure() //если воркер не получил данных то он возвращает фейл и выходит
        }
        //Иницализируем ДБ с двумя таблицами
        val dbpw = AppDb.getInstance(context = applicationContext).postWorkDao()
        val repository: PostRepository =
            PostRepositoryImpl(
                AppDb.getInstance(context = applicationContext).postDao(),
                dbpw ,
            )
        return try {
            Log.e("exc", "WORKER doWork for ${id}")
            repository.processWork(id)
            Log.e("exc", "WORKER DONE")
            dbpw.removeById(id)
            Result.success()

        } catch (e: Exception) {
            Log.e("exc", "WORKER Exception!!")
            Result.retry()
        } catch (e: UnknownError) {
            Log.e("exc", "WORKER failure")
            Result.failure()
        }


    }
}
