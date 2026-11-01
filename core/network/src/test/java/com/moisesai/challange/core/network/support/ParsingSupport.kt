package com.moisesai.challange.core.network.support

/** A track with only the fields every real track has. */
internal fun trackJson(): String =
    """{"wrapperType":"track","trackId":7,"trackName":"Song","artistName":"Artist",""" +
        """"collectionId":9,"collectionName":"Album","trackTimeMillis":1000,"trackNumber":1}"""

internal fun responseJson(vararg items: String): String = """{"results":[${items.joinToString(",")}]}"""
