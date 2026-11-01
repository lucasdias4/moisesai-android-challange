package com.moisesai.challange.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.moisesai.challange.core.database.dao.RecentlyPlayedTrackDao
import com.moisesai.challange.core.database.dao.SavedSearchDao
import com.moisesai.challange.core.database.dao.TrackDao
import com.moisesai.challange.core.database.entity.DbRecentlyPlayedTrack
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbSavedSearchTrack
import com.moisesai.challange.core.database.entity.DbTrack

@Database(
    entities = [DbTrack::class, DbSavedSearch::class, DbSavedSearchTrack::class, DbRecentlyPlayedTrack::class],
    version = 1,
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun savedSearchDao(): SavedSearchDao

    abstract fun recentlyPlayedTrackDao(): RecentlyPlayedTrackDao
}
