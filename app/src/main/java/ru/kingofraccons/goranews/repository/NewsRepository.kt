package ru.kingofraccons.goranews.repository

import ru.kingofraccons.goranews.repository.workwithcache.ArticleDatabase
import ru.kingofraccons.goranews.repository.workwithcache.ArticlesWithCategory
import ru.kingofraccons.goranews.repository.workwithinternet.remotedatasource.RetrofitClient

// repository for get news from network and DB
class NewsRepository(private val articleDatabase: ArticleDatabase) {
    private val newsService = RetrofitClient.getNewsService()
    private val apiKey = "9257f6e27e254fd8a6dd5137640adb25"

    fun getAllNewsOnCategory(category: String) = newsService.getAllNewsOnCategory(category, apiKey)

    suspend fun saveArticles(articles: ArticlesWithCategory) =
        articleDatabase.articleDao().insertArticles(articles)
    fun loadArticles() = articleDatabase.articleDao().getAllArticle()
}