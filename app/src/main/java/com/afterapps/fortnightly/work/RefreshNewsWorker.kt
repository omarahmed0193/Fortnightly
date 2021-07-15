package com.afterapps.fortnightly.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.afterapps.fortnightly.reporsitory.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit


const val CATEGORY_KEY = "categoryKey"

@HiltWorker
class RefreshNewsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        fun enqueueRefreshNewsWorker(context: Context, category: String) {

            val constraints = Constraints.Builder().apply {
                setRequiredNetworkType(NetworkType.UNMETERED)
                setRequiresBatteryNotLow(true)
            }.build()

            val data = workDataOf(CATEGORY_KEY to category)

            val request = PeriodicWorkRequestBuilder<RefreshNewsWorker>(1, TimeUnit.HOURS).apply {
                setConstraints(constraints)
                setInputData(data)
            }.build()

            val uniqueWorkerName = "${category}NewsRefreshWorker"

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkerName,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }


    override suspend fun doWork(): Result {
        val category = inputData.getString(CATEGORY_KEY)

        try {
            category?.let { newsRepository.refreshArticlesForCategory(it) }
        } catch (throwable: Throwable) {
            return Result.retry()
        }
        return Result.success()
    }
}