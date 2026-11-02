package com.moisesai.challange.core.design.ambient

import android.content.Context
import androidx.compose.ui.graphics.Color
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap

// A cover is shrunk to this many pixels on a side before its colours are averaged. The decoder shrinks big JPEGs by
// whole fractions by averaging, so this is a real average of the picture and not a few pixels picked from it.
private const val SAMPLE_SIZE_PX = 64

/**
 * The ambient light colour for the artwork at [url], or null when the picture could not be loaded. It is read
 * through [imageLoader], so a cover that is already shown costs no new download.
 */
suspend fun loadArtworkTint(context: Context, imageLoader: ImageLoader, url: String): Color? {
    val request = ImageRequest.Builder(context)
        .data(url)
        .size(SAMPLE_SIZE_PX)
        // The pixels are read on the CPU, which a hardware bitmap does not allow.
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(request) as? SuccessResult ?: return null
    val bitmap = result.image.toBitmap()
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    return ambientTintOf(averageColorOf(pixels))
}
