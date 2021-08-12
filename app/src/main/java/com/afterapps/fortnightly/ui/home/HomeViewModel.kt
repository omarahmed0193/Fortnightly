package com.afterapps.fortnightly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afterapps.fortnightly.reporsitory.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


const val DEFAULT_ARTICLE_CATEGORY = "business"

@HiltViewModel
class HomeViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel(),
    NewsArticleReactor {

    sealed class Event {
        data class NavigateToDetails(val articleKey: String) : Event()
    }

    private val eventsChannel = Channel<Event>()
    val events = eventsChannel.receiveAsFlow()

    private val articleCategoryChannel = Channel<String>()
    private val articleCategoryFlow = articleCategoryChannel.receiveAsFlow()

    val newsArticlesFlow = articleCategoryFlow.flatMapLatest { category ->
        newsRepository.getArticles(category)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            articleCategoryChannel.send(DEFAULT_ARTICLE_CATEGORY)
        }
    }

    override fun onNewsArticleClick(articleKey: String) {
        viewModelScope.launch {
            eventsChannel.send(Event.NavigateToDetails(articleKey))
        }
    }

    fun onCategorySelected(category: String) {
        val formattedCategory = category.substring(1, category.length)
        viewModelScope.launch {
            articleCategoryChannel.send(formattedCategory)
        }
    }
}