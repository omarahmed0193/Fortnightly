package com.afterapps.fortnightly.model.domain

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey
    val url: String,
    val author: String,
    val content: String,
    val description: String,
    val lastFetchTimeStamp: Long,
    val source: String,
    val title: String,
    val imgUrl: String,
    val publishedAt: String,
    val articleCategory: String
)

object NewsArticlesDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
    override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
        return oldItem == newItem
    }

}