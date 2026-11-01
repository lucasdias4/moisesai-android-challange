package com.moisesai.challange.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * A search whose results were saved. [fetchedAtMillis] is when they were downloaded, so they can be
 * judged fresh or stale. The text is the key exactly as typed; normalising it is the caller's job.
 */
@Entity(tableName = "saved_search")
data class DbSavedSearch(
    @PrimaryKey val searchText: String,
    val fetchedAtMillis: Long,
)
