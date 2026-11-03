package com.moisesai.challange.core.design.extension

// iTunes gives a 100 pixel cover and serves the same image at any size by changing this part of the url.
private const val ITUNES_COVER_SIZE = "100x100bb"
private const val MEDIUM_COVER_SIZE = "200x200bb"
private const val LARGE_COVER_SIZE = "600x600bb"

/** The cover at 200 pixels, for the rows of a list. */
fun String.toMediumCoverUrl(): String = replace(ITUNES_COVER_SIZE, MEDIUM_COVER_SIZE)

/** The cover at 600 pixels, for the big pictures: the song card, the album header and the player. */
fun String.toLargeCoverUrl(): String = replace(ITUNES_COVER_SIZE, LARGE_COVER_SIZE)
