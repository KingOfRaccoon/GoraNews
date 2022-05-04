package ru.kingofraccons.goranews.repository.workwithinternet.interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.kingofraccons.goranews.model.ListArticles

// interface for retrofit
interface NewsService {
    @GET("v2/top-headlines/")
    fun getAllNewsOnCategory(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Call<ListArticles>
}
