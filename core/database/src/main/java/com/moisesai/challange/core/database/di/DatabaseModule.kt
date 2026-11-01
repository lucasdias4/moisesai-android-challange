package com.moisesai.challange.core.database.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.dao.RecentlyPlayedTrackDao
import com.moisesai.challange.core.database.dao.SavedSearchDao
import com.moisesai.challange.core.database.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    private const val DATABASE_NAME = "moisesai.db"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .setDriver(AndroidSQLiteDriver())
            .build()

    @Provides
    fun provideTrackDao(database: AppDatabase): TrackDao = database.trackDao()

    @Provides
    fun provideSavedSearchDao(database: AppDatabase): SavedSearchDao = database.savedSearchDao()

    @Provides
    fun provideRecentlyPlayedTrackDao(database: AppDatabase): RecentlyPlayedTrackDao =
        database.recentlyPlayedTrackDao()
}
