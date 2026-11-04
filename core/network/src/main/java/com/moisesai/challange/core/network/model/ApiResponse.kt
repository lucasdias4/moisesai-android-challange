package com.moisesai.challange.core.network.model

import kotlinx.serialization.Serializable

/**
 * Search and lookup share one envelope. `results` mixes kinds: a lookup by collection id returns
 * the album first (`wrapperType: collection`) and then its tracks.
 */
@Serializable
data class ApiResponse(val results: List<ApiItem>)
