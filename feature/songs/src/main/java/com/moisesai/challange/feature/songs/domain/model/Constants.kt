package com.moisesai.challange.feature.songs.domain.model

internal object Constants {

    // Tracks shown per page. It is the same for a search, a refresh and every next page, because a page starts at
    // `page * SEARCH_PAGE_SIZE` in the saved search: two sizes would skip or repeat songs. 20 is enough to fill a
    // screen and keep scrolling smooth, and small enough to render quickly.
    const val SEARCH_PAGE_SIZE = 20
}
