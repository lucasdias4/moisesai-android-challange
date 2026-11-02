package com.moisesai.challange.feature.songs.domain.repository

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

internal interface SuggestedTracksRepository {

    /**
     * The tracks with these ids, in the same order. A track that cannot be found is left out; it only fails
     * when none of them could be found because of an error.
     */
    suspend fun getSuggestedTracks(trackIds: List<Long>): Result<List<TrackDomainModel>>
}
