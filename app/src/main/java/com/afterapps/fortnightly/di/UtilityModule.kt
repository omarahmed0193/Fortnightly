package com.afterapps.fortnightly.di

import com.afterapps.fortnightly.util.TimeUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {

    @Provides
    @Singleton
    fun provideTimeUtils(): TimeUtil {
        return TimeUtil()
    }
}