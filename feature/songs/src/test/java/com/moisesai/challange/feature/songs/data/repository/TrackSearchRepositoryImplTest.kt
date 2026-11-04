package com.moisesai.challange.feature.songs.data.repository

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.common.TimeProvider
import com.moisesai.challange.core.database.datasource.SavedSearchLocalDataSource
import com.moisesai.challange.core.database.entity.DbSavedSearch
import com.moisesai.challange.core.database.entity.DbTrack
import com.moisesai.challange.core.network.datasource.TrackRemoteDataSource
import com.moisesai.challange.core.network.model.ApiResponse
import com.moisesai.challange.core.network.model.ApiTrack
import com.moisesai.challange.feature.songs.fixture.aDbTrack
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.fixture.anApiTrack
import com.moisesai.challange.feature.songs.support.aDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackSearchRepositoryImplTest {

    private val remoteDataSource = mockk<TrackRemoteDataSource>()
    private val localDataSource = mockk<SavedSearchLocalDataSource>()
    private val repository = TrackSearchRepositoryImpl(
        trackRemoteDataSource = remoteDataSource,
        savedSearchLocalDataSource = localDataSource,
        timeProvider = TimeProvider { NOW_MILLIS },
        dispatchers = aDispatcherProvider(UnconfinedTestDispatcher()),
    )

    @Test
    fun `GIVEN a saved search, WHEN searching the first page, THEN the saved tracks are returned without a request`() = runTest {
        // Given
        givenSavedSearch()
        givenSavedPage(tracks = listOf(aDbTrack()))

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel()), page.tracks)
        coVerify(exactly = 0) { remoteDataSource.searchTracksBySearchText(any(), any()) }
    }

    @Test
    fun `GIVEN no saved search and a working network, WHEN searching the first page, THEN the search is fetched and saved`() = runTest {
        // Given
        givenNoSavedSearch()
        givenNetworkReturns(listOf(anApiTrack()))
        givenSaveSucceeds()
        givenSavedPage(tracks = listOf(aDbTrack()))

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel()), page.tracks)
        coVerify { localDataSource.saveSearch(DbSavedSearch(SEARCH_TEXT, NOW_MILLIS), listOf(aDbTrack())) }
    }

    @Test
    fun `GIVEN no saved search and a working network, WHEN searching, THEN the request asks for the most results iTunes allows`() =
        runTest {
            // Given
            givenNoSavedSearch()
            givenNetworkReturns(emptyList())
            givenSaveSucceeds()
            givenSavedPage(tracks = emptyList())

            // When
            repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE)

            // Then
            coVerify { remoteDataSource.searchTracksBySearchText(SEARCH_TEXT, EXPECTED_FETCH_LIMIT) }
        }

    @Test
    fun `GIVEN no saved search and a failing network, WHEN searching the first page, THEN the failure is returned and nothing is saved`() =
        runTest {
            // Given
            val error = AppError.Network()
            givenNoSavedSearch()
            coEvery { remoteDataSource.searchTracksBySearchText(any(), any()) } returns Result.failure(error)

            // When
            val result = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE)

            // Then
            assertTrue(result.exceptionOrNull() === error)
            coVerify(exactly = 0) { localDataSource.saveSearch(any(), any()) }
        }

    @Test
    fun `GIVEN a search text with capitals and spaces, WHEN searching, THEN the normalised text is the term and the saved key`() = runTest {
        // Given
        givenNoSavedSearch()
        givenNetworkReturns(listOf(anApiTrack()))
        givenSaveSucceeds()
        givenSavedPage(tracks = listOf(aDbTrack()))

        // When
        repository.searchTracksBySearchText("  Daft   Punk ", page = 0, pageSize = PAGE_SIZE)

        // Then
        coVerify { remoteDataSource.searchTracksBySearchText("daft punk", any()) }
        coVerify { localDataSource.saveSearch(DbSavedSearch("daft punk", NOW_MILLIS), any()) }
    }

    @Test
    fun `GIVEN a later page, WHEN searching, THEN the saved tracks at that offset are returned without a request`() = runTest {
        // Given
        givenSavedPage(tracks = listOf(aDbTrack()), offset = 2 * PAGE_SIZE)

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 2, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel()), page.tracks)
        coVerify(exactly = 0) { remoteDataSource.searchTracksBySearchText(any(), any()) }
    }

    @Test
    fun `GIVEN more saved tracks than a page, WHEN searching, THEN the page is full and a next page exists`() = runTest {
        // Given
        givenSavedSearch()
        givenSavedPage(tracks = (1L..PAGE_SIZE + 1L).map { aDbTrack(id = it) })

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertEquals(PAGE_SIZE, page.tracks.size)
        assertTrue(page.hasNext)
    }

    @Test
    fun `GIVEN exactly a page of saved tracks, WHEN searching, THEN there is no next page`() = runTest {
        // Given
        givenSavedSearch()
        givenSavedPage(tracks = (1L..PAGE_SIZE.toLong()).map { aDbTrack(id = it) })

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertFalse(page.hasNext)
    }

    @Test
    fun `GIVEN iTunes finds nothing, WHEN searching, THEN an empty search is saved and an empty page is returned`() = runTest {
        // Given
        givenNoSavedSearch()
        givenNetworkReturns(emptyList())
        givenSaveSucceeds()
        givenSavedPage(tracks = emptyList())

        // When
        val page = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE).getOrThrow()

        // Then
        assertTrue(page.tracks.isEmpty())
        assertFalse(page.hasNext)
        coVerify { localDataSource.saveSearch(DbSavedSearch(SEARCH_TEXT, NOW_MILLIS), emptyList()) }
    }

    @Test
    fun `GIVEN saving the search fails, WHEN searching, THEN the failure is returned`() = runTest {
        // Given
        val error = AppError.Unknown()
        givenNoSavedSearch()
        givenNetworkReturns(listOf(anApiTrack()))
        coEvery { localDataSource.saveSearch(any(), any()) } returns Result.failure(error)

        // When
        val result = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE)

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }

    @Test
    fun `GIVEN reading the saved search fails, WHEN searching the first page, THEN the failure is returned`() = runTest {
        // Given
        val error = AppError.Unknown()
        coEvery { localDataSource.getSavedSearch(any()) } returns Result.failure(error)

        // When
        val result = repository.searchTracksBySearchText(SEARCH_TEXT, page = 0, pageSize = PAGE_SIZE)

        // Then
        assertTrue(result.exceptionOrNull() === error)
    }

    @Test
    fun `GIVEN a saved search, WHEN refreshing, THEN the search is fetched again and saved over the old one`() = runTest {
        // Given
        givenSavedSearch()
        givenNetworkReturns(listOf(anApiTrack()))
        givenSaveSucceeds()
        givenSavedPage(tracks = listOf(aDbTrack()))

        // When
        val page = repository.refreshTracksBySearchText(SEARCH_TEXT, PAGE_SIZE).getOrThrow()

        // Then
        assertEquals(listOf(aTrackDomainModel()), page.tracks)
        coVerify { localDataSource.saveSearch(DbSavedSearch(SEARCH_TEXT, NOW_MILLIS), listOf(aDbTrack())) }
    }

    @Test
    fun `GIVEN a saved search and a failing network, WHEN refreshing, THEN the failure is returned and the saved search is kept`() =
        runTest {
            // Given
            val error = AppError.Network()
            givenSavedSearch()
            coEvery { remoteDataSource.searchTracksBySearchText(any(), any()) } returns Result.failure(error)

            // When
            val result = repository.refreshTracksBySearchText(SEARCH_TEXT, PAGE_SIZE)

            // Then
            assertTrue(result.exceptionOrNull() === error)
            coVerify(exactly = 0) { localDataSource.saveSearch(any(), any()) }
        }

    private fun givenSavedSearch() {
        coEvery { localDataSource.getSavedSearch(SEARCH_TEXT) } returns Result.success(DbSavedSearch(SEARCH_TEXT, 1L))
    }

    private fun givenNoSavedSearch() {
        coEvery { localDataSource.getSavedSearch(any()) } returns Result.success(null)
    }

    private fun givenNetworkReturns(tracks: List<ApiTrack>) {
        coEvery { remoteDataSource.searchTracksBySearchText(any(), any()) } returns Result.success(ApiResponse(tracks))
    }

    private fun givenSaveSucceeds() {
        coEvery { localDataSource.saveSearch(any(), any()) } returns Result.success(Unit)
    }

    private fun givenSavedPage(tracks: List<DbTrack>, offset: Int = 0) {
        coEvery {
            localDataSource.getSavedSearchTracksPage(any(), limit = PAGE_SIZE + 1, offset = offset)
        } returns Result.success(tracks)
    }

    private companion object {
        const val SEARCH_TEXT = "levitating"
        const val PAGE_SIZE = 20
        const val NOW_MILLIS = 1_700_000_000_000L
        const val EXPECTED_FETCH_LIMIT = 200
    }
}
