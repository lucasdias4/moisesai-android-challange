package com.moisesai.challange.feature.songs.domain.model

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

/** One page of search results. [hasNext] tells the caller whether asking for the next page can return more. */
internal data class TrackPageDomainModel(val tracks: List<TrackDomainModel>, val hasNext: Boolean)
