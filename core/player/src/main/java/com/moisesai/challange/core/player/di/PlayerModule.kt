package com.moisesai.challange.core.player.di

import com.moisesai.challange.core.player.ExoPlayerController
import com.moisesai.challange.core.player.PlayerController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PlayerModule {

    @Binds
    abstract fun bindPlayerController(implementation: ExoPlayerController): PlayerController
}
