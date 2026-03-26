package com.duhan.videototext.Data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duhan.videototext.Data.LocalDataSource.AppDatabase
import com.duhan.videototext.Data.LocalDataSource.CategoriesDao
import com.duhan.videototext.Data.LocalDataSource.CategoriesModel
import com.duhan.videototext.Data.LocalDataSource.SearchHistoryModelDao
import com.duhan.videototext.Data.RemoteDataSource.VideoToTextApiService
import com.duhan.videototext.Presentation.MainActivityViewModel
import com.duhan.videototext.Repository.CategoriesRepository
import com.duhan.videototext.Repository.SearchHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    val MIGRATION_3_4 = object : Migration(3, 4) { // 1. versiyondan 2. versiyona geçiş. Veritabanıyla ilgili işlem yaptığımızda
        //yeni bir sütün ekleme, tablo ekleme gibi işlemler için bu migration'ı kullanacağız. Ve versiyon numarasını artıracağız.

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE `summaryForCategories` (
                    `summaryId` INTEGER NOT NULL,
                    `categoryId` INTEGER NOT NULL,
                    PRIMARY KEY(`summaryId`, `categoryId`),
                    FOREIGN KEY(`summaryId`) REFERENCES `summaryText`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_summaryForCategories_categoryId` ON `summaryForCategories` (`categoryId`)")

            db.execSQL("CREATE TABLE `summaryText_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `url` TEXT NOT NULL, `summaryText` TEXT NOT NULL, `videoId` TEXT, `video_title` TEXT)")
            db.execSQL("INSERT INTO `summaryText_new` (id, url, summaryText, videoId, video_title) SELECT id, url, summaryText, videoId, video_title FROM `summaryText`")
            db.execSQL("DROP TABLE `summaryText`")
            db.execSQL("ALTER TABLE `summaryText_new` RENAME TO `summaryText`")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_summaryText_url` ON `summaryText` (`url`)")

        }
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        categoriesDaoProvider: Provider<CategoriesDao>

    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
            .addMigrations(MIGRATION_3_4)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoriesDao = categoriesDaoProvider.get()
                        categoriesDao.insertCategory(CategoriesModel(id = MainActivityViewModel.FavoritesCategoryId, categoryName = "Favorites"))
                        categoriesDao.insertCategory(CategoriesModel(id = MainActivityViewModel.DownloadsCategoryId, categoryName = "Downloads"))
                    }
                }
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideSummaryTextModelDao(appDatabase: AppDatabase) = appDatabase.summaryTextModelDao()

    @Singleton
    @Provides
    fun provideCategoriesDao(appDatabase: AppDatabase) = appDatabase.categoriesDao()

    @Singleton
    @Provides
    fun provideSearchHistoryDao(appDatabase: AppDatabase) = appDatabase.searchHistoryDao()


    @Provides
    fun provideCategoriesRepository(
        categoriesDao: CategoriesDao
    ): CategoriesRepository {
        return CategoriesRepository(categoriesDao)
    }

    @Provides
    fun provideSearchHistoryRepository(
        searchHistoryModelDao: SearchHistoryModelDao
    ): SearchHistoryRepository {
        return SearchHistoryRepository(searchHistoryModelDao)
    }

    @Provides
    @Singleton
    fun provideApiService(): VideoToTextApiService {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoToTextApiService::class.java)
    }

}
//splash