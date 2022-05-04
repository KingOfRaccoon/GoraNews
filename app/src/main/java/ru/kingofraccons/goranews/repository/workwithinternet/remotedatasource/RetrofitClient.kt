package ru.kingofraccons.goranews.repository.workwithinternet.remotedatasource

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kingofraccons.goranews.repository.workwithinternet.interfaces.NewsService

// retrofit object
object RetrofitClient {
    private const val baseUrl = "https://newsapi.org/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }

    fun getNewsService(): NewsService = retrofit.create(NewsService::class.java)
}