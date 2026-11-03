package com.moisesai.challange.feature.songdetails.domain.model

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel

/** The tracks the player can move between, and which of them is the one playing. */
internal data class TrackQueueDomainModel(
    val tracks: List<TrackDomainModel>,
    val currentIndex: Int,
) {
    init {
        require(currentIndex in tracks.indices) { "The current track must be one of the tracks of the queue." }
    }

    val currentTrack: TrackDomainModel get() = tracks[currentIndex]
    val hasNextTrack: Boolean get() = currentIndex < tracks.lastIndex
    val hasPreviousTrack: Boolean get() = currentIndex > 0

    /** The same queue one track later, or this queue when the current track is the last one. */
    fun movedToNextTrack(): TrackQueueDomainModel = if (hasNextTrack) copy(currentIndex = currentIndex + 1) else this

    /** The same queue one track earlier, or this queue when the current track is the first one. */
    fun movedToPreviousTrack(): TrackQueueDomainModel = if (hasPreviousTrack) copy(currentIndex = currentIndex - 1) else this

    companion object {
        /**
         * The queue of the album of [chosenTrack], standing on it. A track of the album that has no audio to play
         * is left out, because the player could not play it; the chosen track stays even then.
         * When [albumTracks] does not hold the chosen track, the queue is only the chosen track.
         */
        fun startingAt(chosenTrack: TrackDomainModel, albumTracks: List<TrackDomainModel>): TrackQueueDomainModel {
            val playableTracks = albumTracks.filter { it.id == chosenTrack.id || it.previewUrl != null }
            val chosenIndex = playableTracks.indexOfFirst { it.id == chosenTrack.id }
            return if (chosenIndex == NOT_FOUND) TrackQueueDomainModel(listOf(chosenTrack), currentIndex = 0) else TrackQueueDomainModel(playableTracks, chosenIndex)
        }

        private const val NOT_FOUND = -1
    }
}
