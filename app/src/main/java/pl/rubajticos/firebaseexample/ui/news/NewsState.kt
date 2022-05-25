package pl.rubajticos.firebaseexample.ui.news

import pl.rubajticos.firebaseexample.model.News

data class NewsState(
    val loading: Boolean = false,
    val news: List<News>
)
