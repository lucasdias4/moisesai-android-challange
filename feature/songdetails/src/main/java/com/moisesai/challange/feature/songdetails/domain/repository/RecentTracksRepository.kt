package com.moisesai.challange.feature.songdetails.domain.repository

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

internal interface RecentTracksRepository {

    /** Remembers that [track] was played just now, so the songs screen can list it. */
    suspend fun saveRecentTrack(track: TrackDomainModel): Result<Unit>
}
