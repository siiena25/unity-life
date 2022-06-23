package com.example.unitylife.di.modules.local

import androidx.annotation.NonNull
import com.example.unitylife.data.repository.EventRepository
import com.example.unitylife.data.source.remote.EventsRemoteDataSource
import com.example.unitylife.di.scope.EventScope
import com.example.unitylife.di.scope.LoginScope
import com.example.unitylife.network.services.EventService
import dagger.Module
import dagger.Provides

@Module
class EventsModule {

    @Provides
    @EventScope
    @NonNull
    fun provideRepository(
        eventsRemoteDataSource: EventsRemoteDataSource
    ): EventRepository = EventRepository(eventsRemoteDataSource)

    @Provides
    @EventScope
    @NonNull
    fun provideRemoteDataSource(
        eventService: EventService
    ): EventsRemoteDataSource = EventsRemoteDataSource(eventService)
}