package com.moisesai.challange.feature.album.presentation.mapper

import com.moisesai.challange.core.design.extension.mainArtist
import com.moisesai.challange.core.design.extension.toLargeCoverUrl
import com.moisesai.challange.core.design.extension.toMediumCoverUrl
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.album.domain.model.AlbumWithTracksDomainModel
import com.moisesai.challange.feature.album.presentation.model.UiAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumTrack

internal fun AlbumWithTracksDomainModel.toUiModel() = UiAlbum(
    name = album.name,
    artistName = album.artistName,
    artUrl = album.artUrl?.toLargeCoverUrl(),
    tracks = tracks.map(TrackDomainModel::toUiAlbumTrack),
)

private fun TrackDomainModel.toUiAlbumTrack() = UiAlbumTrack(
    id = id,
    name = name,
    artistName = artistName.mainArtist(),
    artUrl = artUrl?.toMediumCoverUrl(),
)
