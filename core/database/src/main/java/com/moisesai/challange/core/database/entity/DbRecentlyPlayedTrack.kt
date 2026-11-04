package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

/** A track the user played, and when. The track is the key, so playing it again only moves it to the top. */
@Entity(
    tableName = "recently_played_track",
    foreignKeys = [
        ForeignKey(
            entity = DbTrack::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal data class DbRecentlyPlayedTrack(@PrimaryKey val trackId: Long, val playedAtMillis: Long)
