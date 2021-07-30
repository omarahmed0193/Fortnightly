package com.afterapps.fortnightly.reporsitory

import android.content.Context
import androidx.room.withTransaction
import com.afterapps.fortnightly.database.FortnightlyDatabase
import com.afterapps.fortnightly.model.datatransfer.NewsResponse
import com.afterapps.fortnightly.model.datatransfer.asDomainNewsArticle
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.network.FortnightlyService
import com.afterapps.fortnightly.util.Resource
import com.afterapps.fortnightly.util.TimeUtil
import com.afterapps.fortnightly.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val context: Context,
    private val fortnightlyService: FortnightlyService,
    private val fortnightlyDatabase: FortnightlyDatabase,
    private val timeUtil: TimeUtil
) {

    private val fortnightlyNewsDao = fortnightlyDatabase.fortnightlyNewsDao()

    fun getArticles(category: String): Flow<Resource<List<NewsArticle>>> {
//        RefreshNewsWorker.enqueueRefreshNewsWorker(context, category)
        return networkBoundResource(
            query = { fortnightlyNewsDao.getAllArticles(category) },
            fetch = { fortnightlyService.getTopHeadlineNews(category) },
            saveFetchResult = { newsResponse -> saveArticlesToDatabase(newsResponse, category) },
            shouldFetch = { cachedArticles ->
                val lastRefreshTimestamp =
                    cachedArticles.minByOrNull { it.lastFetchTimeStamp }?.lastFetchTimeStamp
                val shouldRefresh = lastRefreshTimestamp == null ||
                        lastRefreshTimestamp < timeUtil.getCurrentSystemTime() - TimeUnit.HOURS.toMillis(
                    1
                )

                shouldRefresh
            }
        )
    }

    suspend fun saveArticlesToDatabase(newsResponse: NewsResponse, category: String) {
        val newsArticles = newsResponse.articles.map {
            it.asDomainNewsArticle(
                category,
                timeUtil.getCurrentSystemTime()
            )
        }
        fortnightlyDatabase.withTransaction {
            fortnightlyNewsDao.deleteAllArticles(category)
            fortnightlyNewsDao.insertArticles(*newsArticles.toTypedArray())
        }
    }

    fun getArticle(articleKey: String) = fortnightlyNewsDao.getArticle(articleKey)

    suspend fun refreshArticlesForCategory(category: String) {
        val articlesResponse = fortnightlyService.getTopHeadlineNews(category)
        saveArticlesToDatabase(articlesResponse, category)
    }

}