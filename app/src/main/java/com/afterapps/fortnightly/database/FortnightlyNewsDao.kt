package com.afterapps.fortnightly.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afterapps.fortnightly.model.domain.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface FortnightlyNewsDao {

    @Query("SELECT * FROM news_articles WHERE articleCategory = :category")
    fun getAllArticles(category: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE url = :articleKey")
    fun getArticle(articleKey: String): Flow<NewsArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(vararg article: NewsArticle)

    @Query("DELETE FROM news_articles WHERE articleCategory = :category")
    suspend fun deleteAllArticles(category: String)
}