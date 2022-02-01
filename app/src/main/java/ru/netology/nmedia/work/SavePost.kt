package ru.netology.nmedia.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.netology.nmedia.repository.PostRepository


@HiltWorker
class SavePostWorker @AssistedInject constructor(
    private val repository: PostRepository,
    @Assisted applicationContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "post"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }
//        val repository: PostRepository =
//            PostRepositoryImpl(
//                AppDb.getInstance(context = applicationContext).postDao(),
//                AppDb.getInstance(context = applicationContext).postWorkDao(),
//            )
        return try {
            repository.processWork(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}