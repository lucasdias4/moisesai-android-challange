package com.moisesai.challange.core.network.model

import kotlinx.serialization.Serializable

/** Any `wrapperType` this app does not use (artist, music video...). Kept so parsing never fails on it. */
@Serializable
data object ApiUnknownItem : ApiItem
