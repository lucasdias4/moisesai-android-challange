package com.moisesai.challange.core.network.model

import kotlinx.serialization.Serializable

// The subtype is chosen by `wrapperType` (see ItunesJson), so no hand-written serializer is needed.
@Serializable
sealed interface ApiItem
