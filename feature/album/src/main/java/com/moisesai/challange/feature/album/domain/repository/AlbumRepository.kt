package com.moisesai.challange.feature.album.domain.repository

import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel

internal interface AlbumRepository {

    /** Fails when the album does not exist. An album without tracks is a success with an empty list. */
    suspend fun getAlbumWithTracks(albumId: Long): Result<AlbumWithTracksDomainModel>
}
