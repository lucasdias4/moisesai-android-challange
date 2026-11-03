package com.moisesai.challange.feature.album.presentation.composable.component.preview_helper

import com.moisesai.challange.feature.album.presentation.model.UiAlbum
import com.moisesai.challange.feature.album.presentation.model.UiAlbumTrack

private fun aTrack(id: Long, name: String) = UiAlbumTrack(id = id, name = name, artistName = "Daft Punk", artUrl = null)

// Real tracks of a real album, so the previews look like the app does. The covers are empty: a preview has no network.
internal val SampleAlbum = UiAlbum(
    name = "Discovery",
    artistName = "Daft Punk",
    artUrl = null,
    tracks = listOf(
        aTrack(1, "One More Time"),
        aTrack(2, "Aerodynamic"),
        aTrack(3, "Digital Love"),
        aTrack(4, "Harder, Better, Faster, Stronger"),
        aTrack(5, "Crescendolls"),
        aTrack(6, "Nightvision"),
        aTrack(7, "Superheroes"),
        aTrack(8, "High Life"),
    ),
)
