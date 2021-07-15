package com.afterapps.fortnightly.di

import android.content.Context
import androidx.room.Room
import com.afterapps.fortnightly.database.FortnightlyDatabase
import com.afterapps.fortnightly.database.FortnightlyNewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFortnightlyDatabase(@ApplicationContext appContext: Context): FortnightlyDatabase {
        return Room.databaseBuilder(appContext, FortnightlyDatabase::class.java, "fortnightly.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideFortnightlyNewsDao(fortnightlyDatabase: FortnightlyDatabase): FortnightlyNewsDao {
        return fortnightlyDatabase.fortnightlyNewsDao()
    }
}