package ru.kingofraccons.goranews.repository.workwithcache

import androidx.room.Embedded
import androidx.room.Relation
import ru.kingofraccons.goranews.model.Article
import ru.kingofraccons.goranews.model.Category
import ru.kingofraccons.goranews.model.TypeCategories

// entity for unite category and list of articles
data class ArticlesWithCategory(
    @Embedded
    var category: Category = Category(),
    @Relation(entity = Article::class, parentColumn = "name", entityColumn = "categoryName")
    var articles: List<Article> = listOf()
)