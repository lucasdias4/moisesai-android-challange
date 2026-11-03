package com.moisesai.challange.feature.album.domain.model

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

/** An album together with its tracks, in the order of the album. */
internal data class AlbumWithTracksDomainModel(
    val album: AlbumDomainModel,
    val tracks: List<TrackDomainModel>,
)
