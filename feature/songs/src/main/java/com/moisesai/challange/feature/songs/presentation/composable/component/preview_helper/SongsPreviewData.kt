package com.moisesai.challange.feature.songs.presentation.composable.component.preview_helper

import com.moisesai.challange.feature.songs.presentation.model.UiTrack

internal fun aUiTrack(id: Long, name: String, artistName: String, durationText: String) = UiTrack(
    id = id,
    name = name,
    artistName = artistName,
    artUrl = null,
    largeArtUrl = null,
    durationText = durationText,
)

// Real songs and durations, so the previews look like the app does. The covers are empty: a preview has no network.
internal val SampleTracks = listOf(
    aUiTrack(1, "Levitating", "Dua Lipa", "3:24"),
    aUiTrack(2, "Get Lucky", "Daft Punk", "6:10"),
    aUiTrack(3, "luther", "Kendrick Lamar", "2:58"),
    aUiTrack(4, "Army of Me", "Björk", "3:54"),
    aUiTrack(5, "Let Down", "Radiohead", "5:00"),
    aUiTrack(6, "Call Out My Name", "The Weeknd", "3:48"),
)

internal val SuggestedSample = listOf(
    aUiTrack(10, "As", "Stevie Wonder", "7:08"),
    aUiTrack(11, "ocean eyes", "Billie Eilish", "3:20"),
    aUiTrack(12, "Flower In A Hurricane", "Justice", "3:45"),
)

internal val LongTitleSample = aUiTrack(20, "Levitating (The Blessed Madonna Remix)", "Dua Lipa, Madonna, Missy Elliott", "4:10")
