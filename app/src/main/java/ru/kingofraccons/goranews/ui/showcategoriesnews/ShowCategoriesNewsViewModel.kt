package ru.kingofraccons.goranews.ui.showcategoriesnews

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.*
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.kingofraccons.goranews.model.ListArticles
import ru.kingofraccons.goranews.model.TypeCategories
import ru.kingofraccons.goranews.repository.NewsRepository
import ru.kingofraccons.goranews.repository.workwithcache.ArticlesWithCategory
import ru.kingofraccons.goranews.tools.ConnectivityLiveData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*

// viewModel for fragment show categories
class ShowCategoriesNewsViewModel(
    private val repository: NewsRepository,
    application: Application
) : AndroidViewModel(application) {
    // livedata for articles
    val articlesLiveData = MutableLiveData<Map<String, ListArticles?>>()
    // livedata for network states
    val connectivityLiveData = ConnectivityLiveData(application.applicationContext)

    // load articles from DB and from network if connected to internet
    init {
        loadArticles()
        connectivityLiveData.observeForever {
            if (it.isConnected)
                loadData(0)
            Log.d("internetandwi-fi", it.name)
        }
    }

    // load articles from DB
    private fun loadArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            articlesLiveData.postValue(
                repository.loadArticles().sortedBy { it.category.name }.associate {
                    it.category.name.upperFirstLetter() to ListArticles(
                        it.articles.sortedByDescending { it.publishedAt }.take(20)
                    )
                }
            )
        }
    }

    // save articles and image to cache
    private fun saveArticles(articles: ArticlesWithCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveArticles(articles.copy(articles = articles.articles.map {
                it.copy(
                    urlToImage = getBitmapFromURL(
                        it.urlToImage ?: "",
                        it.title.replace(
                            "[^a-zA-Z0-9]".toRegex(),
                            ""
                        ).lowercase().take(20),
                        getApplication<Application>().applicationContext
                    ).toString()
                )
            }))
        }
    }

    // load articles from network
    private fun loadData(typeCategoriesIndex: Int) {
        val typeCategories = TypeCategories.values()[typeCategoriesIndex]
        repository.getAllNewsOnCategory(typeCategories.category.name).enqueue(object :
            Callback<ListArticles> {
            override fun onResponse(
                call: Call<ListArticles>,
                response: Response<ListArticles>
            ) {
                response.body()?.let { list ->
                    val lastArticles = list.articles.take(20)
                    articlesLiveData.postValue(
                        (articlesLiveData.value?.toMutableMap() ?: mutableMapOf()).apply {
                            this[typeCategories.name] = list.copy(articles = lastArticles)
                        })
                    saveArticles(ArticlesWithCategory(typeCategories.category, lastArticles))

                    if (typeCategoriesIndex != TypeCategories.values().lastIndex)
                        loadData(typeCategoriesIndex + 1)
                }
            }

            override fun onFailure(call: Call<ListArticles>, t: Throwable) {
                println(t.message)
                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    "Проверьте подключение к интернету",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // fun return string with string with upper first letter
    private fun String.upperFirstLetter(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else this
        }
    }

    // save bitmap in cache
    private fun Bitmap.saveToInternalStorage(context : Context, name: String): Uri?{
        // get the context wrapper instance
        val wrapper = ContextWrapper(context)

        // initializing a new file
        // bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)

        // create a file to save the image
        file = File(file, "$name.jpg")

        return try {
            // get the file output stream
            val stream = FileOutputStream(file)

            // compress bitmap
            compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // flush the stream
            stream.flush()

            // close stream
            stream.close()

            // return the saved image uri
            Uri.parse(file.absolutePath)
        } catch (e: IOException){ // catch the exception
            e.printStackTrace()
            null
        }
    }

    // get uri from cache
    private fun getBitmapFromURL(strURL: String, name: String, context: Context): Uri? {
        return try {
            BitmapFactory.decodeStream(URL(strURL).openStream())
                .saveToInternalStorage(context, name)
        } catch (e: Exception){
            null
        }
    }
}