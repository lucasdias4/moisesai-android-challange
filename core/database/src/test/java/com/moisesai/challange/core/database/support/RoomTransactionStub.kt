package com.moisesai.challange.core.database.support

import androidx.room3.TransactionScope
import androidx.room3.withWriteTransaction
import com.moisesai.challange.core.database.AppDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

// The Kotlin file that holds Room's `withWriteTransaction` extension.
private const val ROOM_DATABASE_FILE = "androidx.room3.RoomDatabaseKt"

/**
 * Makes a mocked database run the block of a write transaction right away, with no SQLite behind it. A unit test then
 * sees the calls the datasource makes on the DAOs, in order. Whether a real transaction rolls back is the emulator tests' job.
 * Call [stopStubbingWriteTransactions] when the test ends.
 */
internal fun AppDatabase.runsWriteTransactionsInline() {
    mockkStatic(ROOM_DATABASE_FILE)
    coEvery { withWriteTransaction<Unit>(any()) } coAnswers {
        secondArg<suspend TransactionScope<Unit>.() -> Unit>().invoke(mockk(relaxed = true))
    }
}

internal fun stopStubbingWriteTransactions() = unmockkStatic(ROOM_DATABASE_FILE)
