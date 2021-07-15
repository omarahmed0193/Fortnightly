package com.afterapps.fortnightly.di

import android.content.Context
import com.afterapps.fortnightly.database.FortnightlyDatabase
import com.afterapps.fortnightly.network.FortnightlyService
import com.afterapps.fortnightly.reporsitory.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        @ApplicationContext appContext: Context,
        fortnightlyService: FortnightlyService,
        fortnightlyDatabase: FortnightlyDatabase
    ): NewsRepository {
        return NewsRepository(appContext, fortnightlyService, fortnightlyDatabase)
    }
}