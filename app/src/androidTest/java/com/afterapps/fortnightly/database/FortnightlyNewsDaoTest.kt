package com.afterapps.fortnightly.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RunWith(AndroidJUnit4::class)
@SmallTest
class FortnightlyNewsDaoTest {

    private lateinit var fortnightlyDatabase: FortnightlyDatabase
    private lateinit var fortnightlyNewsDao: FortnightlyNewsDao
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

    @Before
    fun setup() {
        fortnightlyDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FortnightlyDatabase::class.java
        ).allowMainThreadQueries().build()

        fortnightlyNewsDao = fortnightlyDatabase.fortnightlyNewsDao()
    }

    @After
    fun teardown() {
        fortnightlyDatabase.close()
    }

    @Test
    fun insertArticles() {
        runBlocking {
            fortnightlyNewsDao.insertArticles(article)
            fortnightlyNewsDao.getArticle(article.url).test {
                assertThat(expectItem()).isEqualTo(article)
            }
        }
    }

    @Test
    fun deleteAllArticles() {
        runBlocking {
            fortnightlyNewsDao.insertArticles(*articles.toTypedArray())
            fortnightlyNewsDao.deleteAllArticles(article.articleCategory)
            fortnightlyNewsDao.getAllArticles(article.articleCategory).test {
                assertThat(expectItem()).isEmpty()
            }
        }
    }

    @Test
    fun getAllArticles() {
        runBlocking {
            fortnightlyNewsDao.insertArticles(*articles.toTypedArray())
            fortnightlyNewsDao.getAllArticles(article.articleCategory).test {
                assertThat(expectItem()).isEqualTo(articles)
            }
        }
    }

    @Test
    fun getArticle() {
        runBlocking {
            fortnightlyNewsDao.insertArticles(article)
            fortnightlyNewsDao.getArticle(article.url).test {
                assertThat(expectItem()).isEqualTo(article)
            }
        }
    }
}