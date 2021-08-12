package com.afterapps.fortnightly.ui.home

import app.cash.turbine.test
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.reporsitory.NewsRepository
import com.afterapps.fortnightly.util.MainCoroutineRule
import com.afterapps.fortnightly.util.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@RunWith(JUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val rule = MainCoroutineRule()

    @MockK
    lateinit var repository: NewsRepository

    lateinit var viewModel: HomeViewModel

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

    private val resource = Resource.Success(data = articles)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun givenViewModelInitializedThenNewsRepositoryGetArticlesCalledWithDefaultCategory() {
        runBlocking {
            coEvery { repository.getArticles(DEFAULT_ARTICLE_CATEGORY) } answers {
                flow {
                    emit(
                        resource
                    )
                }
            }
            viewModel.newsArticlesFlow.test {
                val result = expectItem()
                coVerify { repository.getArticles(DEFAULT_ARTICLE_CATEGORY) }
                assertThat(result).isInstanceOf(Resource.Success::class.java)
                assertThat(result?.data).isEqualTo(articles)
            }
        }
    }

    @Test
    fun givenCategoryChangedThenNewsRepositoryReturnsNewsArticlesForTheSelectedCategory() {
        runBlocking {
            val category = "#category"
            val formattedCategory = "category"

            coEvery { repository.getArticles(formattedCategory) } answers { flow { emit(resource) } }

            viewModel.onCategorySelected(category)

            viewModel.newsArticlesFlow.test() {
                val result = expectItem()
                coVerify { repository.getArticles(formattedCategory) }
                assertThat(result).isInstanceOf(Resource.Success::class.java)
                assertThat(result?.data).isEqualTo(articles)
            }
        }
    }

    @Test
    fun givenOnNewsArticleClickCalledThenNavigationEventIsEmitted() {
        runBlocking {
            val articleKey = "articleKey"
            viewModel.onNewsArticleClick(articleKey)
            viewModel.events.test {
                val result = expectItem()
                assertThat(result).isInstanceOf(HomeViewModel.Event.NavigateToDetails::class.java)
                assertThat((result as HomeViewModel.Event.NavigateToDetails).articleKey).isEqualTo(
                    articleKey
                )
            }
        }
    }
}