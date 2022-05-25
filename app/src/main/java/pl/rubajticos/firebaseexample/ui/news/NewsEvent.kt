package pl.rubajticos.firebaseexample.ui.news

import pl.rubajticos.firebaseexample.model.News

sealed class NewsEvent {
    data class AddNews(val news: News) : NewsEvent()
}
