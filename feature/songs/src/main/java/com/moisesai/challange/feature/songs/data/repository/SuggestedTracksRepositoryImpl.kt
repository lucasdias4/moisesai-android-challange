package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.database.datasource.TrackLocalDataSource
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.tracks
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.data.mapper.toDb
import com.moisesai.challange.feature.songs.data.mapper.toDomain
import com.moisesai.challange.feature.songs.domain.repository.SuggestedTracksRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Looks each track up in the database first and asks iTunes only for the ones it has never seen, then keeps them.
 * So the first launch costs one request per track and every later launch, even without a network, costs none.
 */
internal class SuggestedTracksRepositoryImpl @Inject constructor(
    private val trackLocalDataSource: TrackLocalDataSource,
    private val trackRemoteDataSource: TrackRemoteDataSource,
) : SuggestedTracksRepository {

    override suspend fun getSuggestedTracks(trackIds: List<Long>): Result<List<TrackDomainModel>> {
        // The lookups do not depend on each other, so they run together.
        val outcomes = coroutineScope { trackIds.map { id -> async { resolveTrack(id) } }.awaitAll() }
        val tracks = outcomes.mapNotNull { it.getOrNull() }
        val firstFailure = outcomes.firstNotNullOfOrNull { it.exceptionOrNull() }
        // A few tracks are enough to show; it only counts as a failure when there is nothing to show.
        return if (tracks.isEmpty() && firstFailure != null) Result.failure(firstFailure) else Result.success(tracks)
    }

    private suspend fun resolveTrack(trackId: Long): Result<TrackDomainModel?> =
        trackLocalDataSource.getTrack(trackId).flatMap { saved ->
            if (saved != null) Result.success(saved.toDomain()) else fetchAndSaveTrack(trackId)
        }

    private suspend fun fetchAndSaveTrack(trackId: Long): Result<TrackDomainModel?> =
        trackRemoteDataSource.getTrack(trackId)
            .map { response -> response.tracks().firstOrNull()?.toDb() }
            .flatMap { track ->
                if (track == null) Result.success(null) else trackLocalDataSource.saveTrack(track).map { track.toDomain() }
            }
}
