package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

/** One track in the saved results of a search, at [position]. Deleting the saved search or the track deletes this row. */
@Entity(
    tableName = "saved_search_track",
    primaryKeys = ["searchText", "position"],
    foreignKeys = [
        ForeignKey(
            entity = DbSavedSearch::class,
            parentColumns = ["searchText"],
            childColumns = ["searchText"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DbTrack::class,
            parentColumns = ["id"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("trackId")],
)
internal data class DbSavedSearchTrack(
    val searchText: String,
    val position: Int,
    val trackId: Long,
)
