package com.moisesai.challange.feature.songs.domain.repository

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import kotlinx.coroutines.flow.Flow

internal interface RecentTracksRepository {

    /** The most recently played tracks, newest first, emitted again whenever they change. */
    fun observeRecentTracks(limit: Int): Flow<Result<List<TrackDomainModel>>>
}
