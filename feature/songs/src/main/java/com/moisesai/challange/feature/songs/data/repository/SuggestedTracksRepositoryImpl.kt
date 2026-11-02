package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.database.datasource.TrackLocalDataSource
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.extension.getOnlyTracks
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
        val trackResults = getTrackResults(trackIds)
        val notNullTracks = trackResults.mapNotNull { it.getOrNull() }
        val isAnyTrackFailed = trackResults.any { it.isFailure }
        // A few tracks are enough to show; it only counts as a failure when there is nothing to show.
        val foundNoTracksAndSomeLookupFailed = notNullTracks.isEmpty() && isAnyTrackFailed
        return if (foundNoTracksAndSomeLookupFailed) {
            Result.failure(trackResults.firstNotNullOf { it.exceptionOrNull() })
        } else {
            Result.success(notNullTracks)
        }
    }

    // The lookups do not depend on each other, so they run together.
    private suspend fun getTrackResults(trackIds: List<Long>): List<Result<TrackDomainModel?>> =
        coroutineScope {
            trackIds.map { trackId ->
                async {
                    getTrack(trackId)
                }
            }.awaitAll()
        }

    // Null when iTunes does not know the track either.
    private suspend fun getTrack(trackId: Long): Result<TrackDomainModel?> {
        val cachedTrack = trackLocalDataSource.getTrack(trackId).getOrElse { return Result.failure(it) }
        return if (cachedTrack != null) {
            Result.success(cachedTrack.toDomain())
        } else {
            getTrackFromApiAndSaveToCache(trackId)
        }
    }

    private suspend fun getTrackFromApiAndSaveToCache(trackId: Long): Result<TrackDomainModel?> {
        val response = trackRemoteDataSource.getTrack(trackId).getOrElse { return Result.failure(it) }
        val track = response.getOnlyTracks().firstOrNull()?.toDb() ?: return Result.success(null)
        return trackLocalDataSource.saveTrack(track).map { track.toDomain() }
    }
}
