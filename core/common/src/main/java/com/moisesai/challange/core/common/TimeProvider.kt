package com.moisesai.challange.core.common

/** Injected instead of reading the system clock directly, so tests can fix the time. */
fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

object SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
