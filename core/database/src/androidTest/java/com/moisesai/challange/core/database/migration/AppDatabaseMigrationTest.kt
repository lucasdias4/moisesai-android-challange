package com.moisesai.challange.core.database.migration

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.moisesai.challange.core.database.AppDatabase
import com.moisesai.challange.core.database.support.aDbAlbum
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/** The database of version 1 is built from the schema that Room exported, then opened with today's code, which migrates it. */
class AppDatabaseMigrationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    @After
    fun deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun GIVEN_a_track_saved_in_version_1_WHEN_the_database_is_opened_with_the_current_version_THEN_the_track_is_kept() = runBlocking {
        createVersion1DatabaseWithTrack()

        val database = openCurrentDatabase()
        val track = database.trackDao().getTrack(TRACK_ID)
        database.close()

        assertNotNull(track)
        assertEquals(TRACK_NAME, track?.name)
        assertEquals(TRACK_ARTIST, track?.artistName)
        assertEquals(TRACK_NUMBER, track?.trackNumber)
    }

    @Test
    fun GIVEN_a_database_migrated_from_version_1_WHEN_an_album_is_saved_THEN_the_new_album_tables_work() = runBlocking {
        createVersion1DatabaseWithTrack()
        val database = openCurrentDatabase()

        database.albumDao().upsertAlbum(aDbAlbum())
        val album = database.albumDao().getAlbum(aDbAlbum().id)
        database.close()

        assertEquals(aDbAlbum(), album)
    }

    private fun createVersion1DatabaseWithTrack() {
        val schema = readSchema(version = 1)
        val sqlite = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null)
        val entities = schema.getJSONArray("entities")
        for (position in 0 until entities.length()) {
            val entity = entities.getJSONObject(position)
            val tableName = entity.getString("tableName")
            sqlite.execSQL(entity.getString("createSql").replace(TABLE_NAME_PLACEHOLDER, tableName))
            // The indices are part of the schema too: Room checks them after the migration.
            val indices = entity.optJSONArray("indices") ?: continue
            for (indexPosition in 0 until indices.length()) {
                sqlite.execSQL(indices.getJSONObject(indexPosition).getString("createSql").replace(TABLE_NAME_PLACEHOLDER, tableName))
            }
        }
        val setupQueries = schema.getJSONArray("setupQueries")
        for (position in 0 until setupQueries.length()) {
            sqlite.execSQL(setupQueries.getString(position))
        }
        sqlite.execSQL(
            "INSERT INTO track (id, name, artistName, albumId, albumName, artUrl, previewUrl, trackTimeMillis, releaseDate, trackNumber) " +
                "VALUES ($TRACK_ID, '$TRACK_NAME', '$TRACK_ARTIST', 100, 'Future Nostalgia', NULL, NULL, 203808, NULL, $TRACK_NUMBER)",
        )
        sqlite.version = 1
        sqlite.close()
    }

    private fun readSchema(version: Int): JSONObject {
        // The schemas are assets of the test APK, not of the app.
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val json = assets.open("$SCHEMA_FOLDER/$version.json").bufferedReader().use { it.readText() }
        return JSONObject(json).getJSONObject("database")
    }

    private fun openCurrentDatabase(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .setDriver(AndroidSQLiteDriver())
            .build()

    private companion object {
        const val DATABASE_NAME = "migration-test.db"
        const val SCHEMA_FOLDER = "com.moisesai.challange.core.database.AppDatabase"
        const val TABLE_NAME_PLACEHOLDER = "\${TABLE_NAME}"
        const val TRACK_ID = 7L
        const val TRACK_NAME = "Levitating"
        const val TRACK_ARTIST = "Dua Lipa"
        const val TRACK_NUMBER = 5
    }
}
