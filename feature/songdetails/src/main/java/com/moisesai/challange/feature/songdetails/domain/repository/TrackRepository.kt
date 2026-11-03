package com.moisesai.challange.feature.songdetails.domain.repository

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

internal interface TrackRepository {

    /** Serves the saved track when there is one, and fetches and saves it otherwise. */
    suspend fun getTrack(trackId: Long): Result<TrackDomainModel>

    /** The tracks of an album, in the order of the album. */
    suspend fun getAlbumTracks(albumId: Long): Result<List<TrackDomainModel>>
}
