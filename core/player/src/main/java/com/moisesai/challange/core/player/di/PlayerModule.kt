package com.moisesai.challange.core.player.di

import com.moisesai.challange.core.player.ExoPlayerController
import com.moisesai.challange.core.player.PlayerController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Public so the end-to-end tests of the app can replace the player with a fake one. */
@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    internal abstract fun bindPlayerController(implementation: ExoPlayerController): PlayerController
}
