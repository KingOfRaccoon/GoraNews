package ru.kingofraccons.goranews.repository.workwithcache

import androidx.room.*
import ru.kingofraccons.goranews.model.Article
import ru.kingofraccons.goranews.model.Category

// dao interface for load data from DB
@Dao
interface ArticleDao {
    @Transaction @Query("Select * from category")
    fun getAllArticle(): List<ArticlesWithCategory>

    @Transaction
    suspend fun insertArticles(articlesWithCategory: ArticlesWithCategory) {
        insertCategory(articlesWithCategory.category)
        articlesWithCategory.articles.forEach { insertArticle(it.copy(categoryName = articlesWithCategory.category.name)) }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: Article)
}