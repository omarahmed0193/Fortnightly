package com.afterapps.fortnightly.network

import com.afterapps.fortnightly.BuildConfig
import com.afterapps.fortnightly.model.datatransfer.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FortnightlyService {

    companion object {
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = BuildConfig.NEWS_API_KEY
        const val API_KEY_PARAMETER = "apiKey"
    }

    @GET("top-headlines?country=us&pageSize=100")
    suspend fun getTopHeadlineNews(@Query("category") category: String): NewsResponse
}