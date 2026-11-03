package com.moisesai.challange.core.design.extension

/** iTunes lists every artist of a song ("Justice, Tame Impala, SZA"); the design shows the first one. */
fun String.mainArtist(): String = substringBefore(",").trim()
