package com.afterapps.fortnightly.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.afterapps.fortnightly.model.domain.NewsArticle

@Database(entities = [NewsArticle::class], version = 1)
abstract class FortnightlyDatabase : RoomDatabase() {
    abstract fun fortnightlyNewsDao(): FortnightlyNewsDao
}