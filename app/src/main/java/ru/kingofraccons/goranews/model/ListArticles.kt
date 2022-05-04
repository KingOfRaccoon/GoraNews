package ru.kingofraccons.goranews.model

// class for get articles from network
data class ListArticles(
    val articles: List<Article>,
    val status: String = "",
    val totalResults: Int = 0
)