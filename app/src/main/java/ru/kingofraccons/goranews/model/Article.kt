package ru.kingofraccons.goranews.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore

// entity article
@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["name"],
        childColumns = ["categoryName"],
        onDelete = ForeignKey.CASCADE)],
    primaryKeys = ["categoryName", "title"]
)
data class Article(
    @Ignore
    var source: NewSource = NewSource("", ""),
    var author: String? = "",
    var content: String? = "",
    var description: String? = "",
    var publishedAt: String = "",
    var title: String = "",
    var url: String = "",
    var urlToImage: String? = "",
    var categoryName: String = ""
)