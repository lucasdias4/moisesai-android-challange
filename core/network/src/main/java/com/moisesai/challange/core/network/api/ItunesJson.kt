package com.moisesai.challange.core.network.api

import com.moisesai.challange.core.network.model.ApiItem
import com.moisesai.challange.core.network.model.ApiUnknownItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

// iTunes answers with `text/javascript` and ~30 fields per item; the converter ignores the
// response content type, and unknown keys must not fail the parse. `explicitNulls = false` is what
// makes an absent nullable field null, so the DTOs need no `= null` defaults. `wrapperType` says which
// DTO an item is; kinds the app does not use fall back to ApiUnknownItem.
internal val ItunesJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    classDiscriminator = "wrapperType"
    serializersModule = SerializersModule {
        polymorphicDefaultDeserializer(ApiItem::class) { ApiUnknownItem.serializer() }
    }
}
