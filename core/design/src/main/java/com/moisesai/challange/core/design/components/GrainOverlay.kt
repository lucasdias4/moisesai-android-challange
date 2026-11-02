package com.moisesai.challange.core.design.components

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import com.moisesai.challange.core.design.R

// The noise is very faint: just enough to break up the smooth gradients and give the screen a film-like texture.
private const val GRAIN_ALPHA = 0.12f

// The tile image is 660 pixels wide and was drawn for a tile of 220 dp (three pixels per dp), so it is scaled
// by the screen density divided by three to keep every grain the size it has in the design.
private const val GRAIN_TILE_DP = 220f
private const val GRAIN_TILE_PIXELS = 660f

/** A film grain over the whole screen. Put it last, above everything, so it textures what is below it. */
@Composable
fun GrainOverlay(modifier: Modifier = Modifier) {
    val tile = ImageBitmap.imageResource(R.drawable.grain_tile)
    val density = LocalDensity.current.density
    val brush = remember(tile, density) { grainBrush(tile, density) }
    Canvas(modifier = modifier.fillMaxSize()) {
        // Overlay lightens the light parts and darkens the dark parts of whatever is already drawn.
        drawRect(brush = brush, alpha = GRAIN_ALPHA, blendMode = BlendMode.Overlay)
    }
}

private fun grainBrush(tile: ImageBitmap, density: Float): Brush = object : ShaderBrush() {
    override fun createShader(size: Size) =
        ImageShader(tile, TileMode.Repeated, TileMode.Repeated).also { shader ->
            val scale = GRAIN_TILE_DP * density / GRAIN_TILE_PIXELS
            shader.setLocalMatrix(Matrix().apply { setScale(scale, scale) })
        }
}
