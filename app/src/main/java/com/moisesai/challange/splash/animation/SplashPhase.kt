package com.moisesai.challange.splash.animation

/**
 * The steps of the splash, in order. Together they last 800 ms, the time the design keeps the splash on screen before it
 * hands over to the first screen.
 */
internal enum class SplashPhase(val durationMillis: Long) {
    // Nothing is shown yet. The splash starts here so that the first step has a state to grow from.
    Waiting(durationMillis = 0),

    // The note scales up and fades in.
    Entering(durationMillis = 500),

    // The note rests for a moment before the next screen takes over.
    Holding(durationMillis = 300),
}
