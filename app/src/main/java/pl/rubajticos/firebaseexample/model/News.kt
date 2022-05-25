package pl.rubajticos.firebaseexample.model

import java.util.*

data class News(
    val id: String,
    val title: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val createdDate: Date
)