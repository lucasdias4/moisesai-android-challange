package com.moisesai.challange.core.database.support

import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import com.moisesai.challange.core.database.AppDatabase

/** A real SQLite database that lives in memory: nothing is written to disk and each test starts empty. */
internal fun createInMemoryDatabase(): AppDatabase =
    Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
        .setDriver(AndroidSQLiteDriver())
        .build()
