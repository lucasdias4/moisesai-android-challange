package com.moisesai.challange.feature.album.domain.usecase

import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.domain.repository.AlbumRepository
import javax.inject.Inject

internal class GetAlbumWithTracksUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
) {

    suspend operator fun invoke(albumId: Long): Result<AlbumWithTracksDomainModel> =
        albumRepository.getAlbumWithTracks(albumId)
}
