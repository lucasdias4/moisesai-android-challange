package com.moisesai.challange.splash.animation

/**
 * The splash is a timeline. SplashScreen walks through these phases in order and
 * every animation just reacts to the current one, so none of them needs its own timer.
 *
 * - Equalizing: the bars bounce.
 * - Collapsing: the bars shrink to dots and the glow appears.
 * - Revealing: the title springs in.
 * - Exiting: everything fades out, then the splash is over.
 *
 * Each phase lasts [durationMillis]. Phases are compared by order:
 * `phase >= Collapsing` means "collapsing or any later phase".
 */
internal enum class SplashPhase(val durationMillis: Long) {
    Equalizing(durationMillis = 1_800),
    Collapsing(durationMillis = 600),
    Revealing(durationMillis = 2_500),
    Exiting(durationMillis = 900),
}
