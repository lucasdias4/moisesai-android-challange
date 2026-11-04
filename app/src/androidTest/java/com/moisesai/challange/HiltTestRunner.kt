package com.moisesai.challange

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/** Starts every instrumented test with the Hilt test application, so the end-to-end tests can replace modules. */
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(classLoader: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(classLoader, HiltTestApplication::class.java.name, context)
}
