package ru.kingofraccons.goranews

import android.app.Application
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import ru.kingofraccons.goranews.repository.NewsRepository
import ru.kingofraccons.goranews.repository.workwithcache.ArticleDatabase
import ru.kingofraccons.goranews.ui.showcategoriesnews.ShowCategoriesNewsViewModel

// class application
class GoraNewsApplication : Application() {
    // modules for inject repository and viewModel
    private val modules = module {
        single {
            Room.databaseBuilder(
                this@GoraNewsApplication,
                ArticleDatabase::class.java,
                "artiles"
            ).build()
        }
        single { NewsRepository(get()) }
        viewModel { ShowCategoriesNewsViewModel(get(), this@GoraNewsApplication) }
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@GoraNewsApplication)
            modules(modules)
        }
    }
}