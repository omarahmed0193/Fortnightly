package com.afterapps.fortnightly.repository

import android.content.Context
import androidx.room.withTransaction
import app.cash.turbine.test
import com.afterapps.fortnightly.R
import com.afterapps.fortnightly.database.FortnightlyDatabase
import com.afterapps.fortnightly.database.FortnightlyNewsDao
import com.afterapps.fortnightly.model.datatransfer.Article
import com.afterapps.fortnightly.model.datatransfer.NewsResponse
import com.afterapps.fortnightly.model.datatransfer.Source
import com.afterapps.fortnightly.model.datatransfer.asDomainNewsArticle
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.network.FortnightlyService
import com.afterapps.fortnightly.reporsitory.NewsRepository
import com.afterapps.fortnightly.util.Resource
import com.afterapps.fortnightly.util.TimeUtil
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@RunWith(JUnit4::class)
@ExperimentalTime
class NewsRepositoryTest {

    private lateinit var repository: NewsRepository

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var networkService: FortnightlyService

    @MockK
    lateinit var timeUtil: TimeUtil

    private val article = NewsArticle(
        "url",
        "author",
        "content",
        "description",
        1,
        "source",
        "title",
        "imgurl",
        "publishedAt",
        "category"
    )
    private val articles = listOf(article)

    private val newsResponse = NewsResponse(
        listOf(
            Article(
                "author",
                "content",
                "description",
                "publishedAt",
                Source("id", "name"),
                "title",
                "url",
                "imgurl"
            )
        ), "status", 1
    )

    @MockK
    lateinit var database: FortnightlyDatabase
    private lateinit var dao: FortnightlyNewsDao

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )

        val transactionLambda = slot<suspend () -> R>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        repository = NewsRepository(context, networkService, database, timeUtil)
        dao = database.fortnightlyNewsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenCachedArticlesExistAndNetworkServiceReturnErrorWhenGetArticlesIsCalledThenErrorResourceReturnedWithCachedArticles() {
        runBlocking {
            every {
                dao.getAllArticles(any())
            } answers { flow { emit(articles) } }

            //Trigger should fetch boolean with true value
            every { timeUtil.getCurrentSystemTime() } answers { TimeUnit.HOURS.toMillis(2) }

            coEvery { networkService.getTopHeadlineNews(any()) } throws Throwable()

            repository.getArticles("").test {
                val result = expectItem()
                assertThat(result).isInstanceOf(Resource.Error::class.java)
                assertThat(result.data).isEqualTo(articles)
                expectComplete()
            }
        }
    }

    @Test
    fun givenCachedArticlesIsEmptyAndNetworkServiceReturnErrorWhenGetArticlesIsCalledThenErrorResourceReturnedWithEmptyList() {
        runBlocking {
            every {
                dao.getAllArticles(any())
            } answers { flow { emit(emptyList()) } }

            //Trigger should fetch boolean with true value
            every { timeUtil.getCurrentSystemTime() } answers { TimeUnit.HOURS.toMillis(2) }

            coEvery { networkService.getTopHeadlineNews(any()) } throws Throwable()

            repository.getArticles("").test {
                val result = expectItem()
                assertThat(result).isInstanceOf(Resource.Error::class.java)
                assertThat(result.data).isEmpty()
                expectComplete()
            }
        }
    }

    @Test
    fun givenCachedArticlesExistAndNetworkShouldNotFetchWhenGetArticlesIsCalledThenSuccessResourceReturnedWithCachedArticles() {
        runBlocking {
            every {
                dao.getAllArticles(any())
            } answers { flow { emit(articles) } }

            //Trigger should fetch boolean with false value
            every { timeUtil.getCurrentSystemTime() } answers { article.lastFetchTimeStamp }

            coEvery { networkService.getTopHeadlineNews(any()) } answers { newsResponse }

            repository.getArticles("").test {
                val result = expectItem()
                assertThat(result).isInstanceOf(Resource.Success::class.java)
                assertThat(result.data).isEqualTo(articles)
                expectComplete()
            }
        }
    }

    @Test
    fun givenCachedArticlesIsEmptyAndNetworkServiceReturnDataWhenGetArticlesIsCalledThenSuccessResourceReturnedWithNetworkArticles() {
        runBlocking {
            val category = "category"
            val currentTime = 1L
            val updatedNewsData = newsResponse.articles.map {
                it.asDomainNewsArticle(
                    category,
                    currentTime
                )
            }

            //Keep lastFetchTimeStamp fixed across this test case
            every { timeUtil.getCurrentSystemTime() } answers { currentTime }

            //First emit the cached data then emit the newly fetched and persisted data
            every {
                dao.getAllArticles(category)
            } answers { flow { emit(emptyList()) } } andThenAnswer { flow { emit(updatedNewsData) } }

            coEvery { networkService.getTopHeadlineNews(category) } returns newsResponse

            repository.getArticles(category).test {
                val result = expectItem()
                assertThat(result).isInstanceOf(Resource.Success::class.java)
                assertThat(result.data).isEqualTo(updatedNewsData)
                expectComplete()
            }

            coVerify { dao.deleteAllArticles(category) }
            coVerify { dao.insertArticles(*updatedNewsData.toTypedArray()) }
        }
    }

    @Test
    fun refreshArticlesForCategory() {
        runBlocking {
            val category = "category"
            val currentTime = 1L
            val updatedNewsData = newsResponse.articles.map {
                it.asDomainNewsArticle(
                    category,
                    currentTime
                )
            }

            //Keep lastFetchTimeStamp fixed across this test case
            every { timeUtil.getCurrentSystemTime() } answers { currentTime }

            coEvery { networkService.getTopHeadlineNews(category) } returns newsResponse

            repository.refreshArticlesForCategory(category)

            coVerify { dao.deleteAllArticles(category) }
            coVerify { dao.insertArticles(*updatedNewsData.toTypedArray()) }
        }
    }
}