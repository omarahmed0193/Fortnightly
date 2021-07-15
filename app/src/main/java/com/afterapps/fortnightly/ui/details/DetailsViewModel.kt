package com.afterapps.fortnightly.ui.details

import androidx.lifecycle.*
import com.afterapps.fortnightly.reporsitory.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    sealed class Event {
        data class OpenArticleUrl(val articleUrl: String) : Event()
    }

    private val eventsChannel = Channel<Event>()
    val events = eventsChannel.receiveAsFlow()

    val article = savedStateHandle.getLiveData<String>("articleKey").switchMap {
        newsRepository.getArticle(it).asLiveData()
    }

    fun onContinueReadingClick() {
        article.value?.let { article ->
            viewModelScope.launch {
                eventsChannel.send(Event.OpenArticleUrl(article.url))
            }
        }
    }
}