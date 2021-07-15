package com.afterapps.fortnightly.model.datatransfer


import com.afterapps.fortnightly.model.domain.NewsArticle
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsResponse(
    @Json(name = "articles")
    val articles: List<Article>,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int
)

@JsonClass(generateAdapter = true)
data class Article(
    @Json(name = "author")
    val author: String? = "",
    @Json(name = "content")
    val content: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "publishedAt")
    val publishedAt: String?,
    @Json(name = "source")
    val source: Source,
    @Json(name = "title")
    val title: String?,
    @Json(name = "url")
    val url: String?,
    @Json(name = "urlToImage")
    val urlToImage: String?
)

@JsonClass(generateAdapter = true)
data class Source(
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?
)

fun Article.asDomainNewsArticle(category: String) = NewsArticle(
    url = url ?: "",
    author = author ?: "",
    content = content?.substringBefore("…") ?: "",
    description = description ?: "",
    lastFetchTimeStamp = System.currentTimeMillis(),
    source = source.name ?: "",
    title = title ?: "",
    imgUrl = urlToImage ?: "",
    publishedAt = publishedAt ?: "",
    articleCategory = category
)