package ru.kingofraccons.goranews.repository.workwithcache

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kingofraccons.goranews.model.Article
import ru.kingofraccons.goranews.model.Category

// class DB for cache data
@Database(entities = [Article::class, Category::class], version = 1)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}