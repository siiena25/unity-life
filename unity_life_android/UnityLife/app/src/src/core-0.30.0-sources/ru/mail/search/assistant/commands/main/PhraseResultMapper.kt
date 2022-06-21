package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.auth.common.domain.model.AuthCompleteAction
import ru.mail.search.assistant.auth.common.domain.model.AuthContext
import ru.mail.search.assistant.auth.common.domain.model.AuthType
import ru.mail.search.assistant.commands.command.media.RepeatMode
import ru.mail.search.assistant.commands.factory.FactoryProvider
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.entities.ControlType
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.ServerQueuedCommand
import ru.mail.search.assistant.entities.message.ActionTag
import ru.mail.search.assistant.entities.message.MessageData

internal class PhraseResultMapper(private val factoryProvider: FactoryProvider) {

    fun map(phraseId: String, commands: List<ServerQueuedCommand>): List<ExecutableCommandData> {
        val result = ArrayList<ExecutableCommandData>()
        commands.forEach { queuedCommand ->
            internalMap(phraseId, queuedCommand.command, queuedCommand.queueType, result)
        }
        return result
    }

    private fun internalMap(
        phraseId: String,
        command: ServerCommand,
        queueType: QueueType,
        result: ArrayList<ExecutableCommandData>
    ) {
        when (command) {
            is ServerCommand.ShowText -> result.showMessage(
                phraseId,
                MessageData.IncomingText(text = command.text)
            )
            is ServerCommand.SpeakOut -> result.sync(
                factoryProvider.commands.playTts(phraseId, command.tts)
            )
            is ServerCommand.ListenCommand -> result.sync(
                factoryProvider.commands.waitForUserInput(
                    command.minWaitingTime,
                    command.muteActivationSound,
                    command.callbackData
                )
            )
            is ServerCommand.ShowUrl -> {
                factoryProvider.commands.showUrl(command.url).let { cmd -> result.sync(cmd) }
            }
            is ServerCommand.ShowFact -> result.showMessage(
                phraseId,
                with(command) {
                    MessageData.Fact(
                        title,
                        text,
                        fullText,
                        link,
                        linkTitle,
                        imageUrl,
                        searchUrl,
                        searchTitle,
                        appRefs
                    )
                }
            )
            is ServerCommand.ShowSearchCard -> result.showMessage(
                phraseId,
                MessageData.ActionCard(
                    command.text,
                    ActionTag.SEARCH,
                    command.linkLabel,
                    command.linkUrl
                )
            )
            is ServerCommand.ShowNewsCard -> result.showMessage(
                phraseId,
                MessageData.NewsCard(command.title, command.news)
            )
            is ServerCommand.ShowWeather -> result.showMessage(
                phraseId,
                with(command) {
                    MessageData.Weather(
                        temperature,
                        city,
                        cityFormed,
                        dateFormed,
                        pressure,
                        windSpeed,
                        humidity,
                        iconType,
                        iconUrl,
                        morning,
                        daytime,
                        night,
                        url
                    )
                }
            )
            is ServerCommand.ShowSlider -> result.showMessage(
                phraseId,
                MessageData.Slider(command.items, command.extensionItem)
            )
            is ServerCommand.ShowMovies -> result.showMessage(
                phraseId,
                MessageData.Movies(command.movies)
            )
            is ServerCommand.ShowMovieTimetable -> result.showMessage(
                phraseId,
                MessageData.MovieTimetable(
                    title = command.title,
                    genres = command.genres,
                    url = command.url,
                    cinemaSessions = command.cinemaSessions
                )
            )
            is ServerCommand.ShowCinemaTheaterTimetable -> result.showMessage(
                phraseId,
                MessageData.CinemaTimetable(
                    title = command.title,
                    address = command.address,
                    url = command.url,
                    movieSessions = command.movieSessions
                )
            )
            is ServerCommand.ShowImages -> result.showMessage(
                phraseId,
                MessageData.PhotoAlbum(command.imageData, command.morePhotosUrl)
            )
            is ServerCommand.PlayDialogSound -> result.sync(
                factoryProvider.commands.playDialogMedia(command.url, command.kwsSkipIntervals)
            )
            is ServerCommand.PlaylistMusic -> result.sync(
                factoryProvider.commands.playMusic(
                    phraseId,
                    command.playlist,
                    command.autoPlay,
                    command.trackNumber,
                    command.trackPosition
                )
            )
            is ServerCommand.PlaylistNoise -> result.sync(
                factoryProvider.commands.playSound(
                    phraseId,
                    command.playlist,
                    command.autoPlay,
                    command.trackNumber,
                    command.trackPosition
                )
            )
            is ServerCommand.PlaylistRadio -> result.sync(
                factoryProvider.commands.playRadio(
                    phraseId,
                    command.playlist,
                    command.autoPlay,
                    command.trackNumber
                )
            )
            is ServerCommand.PlaylistTale -> result.sync(
                factoryProvider.commands.playTale(
                    phraseId,
                    command.playlist,
                    command.autoPlay,
                    command.trackNumber,
                    command.trackPosition
                )
            )
            is ServerCommand.PlaylistPodcast -> result.sync(
                factoryProvider.commands.playPodcasts(
                    phraseId,
                    command.playlist,
                    command.autoPlay,
                    command.trackNumber,
                    command.trackPosition
                )
            )
            is ServerCommand.Delay -> result.sync(
                factoryProvider.commands.awaitUserInputWithTimer(command.timestamp)
            )
            is ServerCommand.Suggests -> result.sync(
                factoryProvider.commands.showSuggests(phraseId, command.list)
            )
            is ServerCommand.Controls -> {
                val controlCommand = when (command) {
                    is ServerCommand.Controls.PlaybackResume ->
                        factoryProvider.commands.resumeMusic()
                    is ServerCommand.Controls.PlaybackPause ->
                        factoryProvider.controls.playPause()
                    is ServerCommand.Controls.PlaybackVolume ->
                        factoryProvider.controls.setVolume(command.volume)
                    is ServerCommand.Controls.PlaybackNext ->
                        factoryProvider.controls.playNext()
                    is ServerCommand.Controls.PlaybackPrev ->
                        factoryProvider.controls.playPrev()
                    is ServerCommand.Controls.PlaybackForward ->
                        factoryProvider.controls.playForward()
                    is ServerCommand.Controls.PlaybackBackward ->
                        factoryProvider.controls.playBackward()
                    is ServerCommand.Controls.PlaybackStop ->
                        factoryProvider.controls.playStop()
                    is ServerCommand.Controls.PlaybackRepeat ->
                        factoryProvider.controls.setRepeat(
                            RepeatMode.values()[command.repeatType],
                            command.count
                        )
                    is ServerCommand.Controls.PlaybackRewind ->
                        factoryProvider.controls.playRewind(
                            command.playlistPosition,
                            command.elapsed
                        )
                }
                when (command.type) {
                    ControlType.COMMAND -> result.sync(controlCommand)
                    ControlType.MEDIA -> result.media(controlCommand)
                }
            }

            is ServerCommand.AuthorizeCard -> {
                val authType: AuthType? = when (command.authType) {
                    "vk" -> AuthType.VK
                    "mail" -> AuthType.MAIL
                    "registration" -> AuthType.REGISTRATION
                    else -> null
                }
                val authContext =
                    AuthContext(authType, AuthCompleteAction.SEND_TEXT, command.repeatText)
                val data =
                    MessageData.AuthorizeCard(text = command.text, authContext = authContext)
                result.showMessage(phraseId, data)
            }
            is ServerCommand.EventListCard -> result.showMessage(
                phraseId,
                MessageData.EventListCard(items = command.skills)
            )
            is ServerCommand.MailCountCard -> result.showMessage(
                phraseId,
                MessageData.ActionCard(
                    text = command.text,
                    tag = ActionTag.MAIL_COUNT,
                    linkLabel = command.linkLabel,
                    linkUrl = command.linkUrl.orEmpty()
                )
            )
            is ServerCommand.CompanyCard -> {
                command.run {
                    result.showMessage(
                        phraseId,
                        MessageData.CompanyCard(
                            title = title,
                            address = address,
                            phones = phones,
                            imageUrl = imageUrl,
                            schedule = schedule,
                            metro = metro,
                            mapUrl = mapUrl,
                            routeUrl = routeUrl,
                            siteUrl = siteUrl,
                            category = category,
                            description = description,
                            distance = distance,
                            rating = rating
                        )
                    )
                }
            }
            is ServerCommand.SerpCard -> {
                command.run {
                    result.showMessage(phraseId, MessageData.SerpCard(items, searchUrl))
                }
            }
            is ServerCommand.Recipes -> result.showMessage(
                phraseId,
                MessageData.RecipesCard(command.items, command.searchUrl)
            )
            is ServerCommand.WelcomeScreenCommand -> result.sync(
                factoryProvider.commands.showWelcomeDialogMessage(
                    MessageData.WelcomeScreenMessage(command.welcomeScreen)
                )
            )
            is ServerCommand.FlowMode -> {
                result.sync(factoryProvider.commands.enterFlowMode(command.name))
            }
            is ServerCommand.ExternalUserDefined -> {
                factoryProvider.external.provideCommandData(queueType, command)
                    ?.let { result.add(it) }
            }

            is ServerCommand.PermissionRequestCard -> {
                result.showMessage(
                    phraseId,
                    MessageData.PermissionRequest(command.text)
                )
            }

            is ServerCommand.PermissionRequestCommand -> {
                result.sync(factoryProvider.commands.requestPermission(command.permission, command.requestCode))
            }

            is ServerCommand.ContactsSync -> {
                result.sync(factoryProvider.commands.syncContacts(command.event))
            }

            is ServerCommand.ContactsCard -> {
                result.sync(
                    factoryProvider.commands.contactsCard(
                        phraseId,
                        command.hasMore,
                        command.page,
                        command.contacts,
                        command.callbackEvent,
                    )
                )
            }

            is ServerCommand.ContactNumbersCard -> {
                result.sync(
                    factoryProvider.commands.contactNumbersCard(
                        phraseId,
                        command.contactId,
                        command.callbackEvent
                    )
                )
            }

            is ServerCommand.PhoneCall -> {
                result.sync(factoryProvider.commands.phoneCall(command.numberId))
            }

            is ServerCommand.PhoneCallCard -> {
                result.sync(factoryProvider.commands.phoneCallCard(phraseId, command.contactId, command.numberId))
            }

            is ServerCommand.EmergencyCall -> {
                result.sync(factoryProvider.commands.emergencyCall(command.phoneNumber))
            }

            is ServerCommand.EmergencyCallCard -> {
                result.sync(factoryProvider.commands.emergencyCallCard(phraseId, command.phoneNumber))
            }

            is ServerCommand.FindPhoneByPart -> {
                result.sync(
                    factoryProvider.commands.findPhoneByPart(
                        command.contactId,
                        command.phonePart,
                        command.callbackEvent
                    )
                )
            }

            is ServerCommand.EditSms -> result.sync(factoryProvider.commands.editSms(command.text))
            is ServerCommand.SendSms -> result.sync(factoryProvider.commands.sendSms(command.numberId, command.text))
        }
    }

    private fun ArrayList<ExecutableCommandData>.sync(command: ExecutableCommand<*>) {
        add(ExecutableCommandData(QueueType.SYNC, command))
    }

    private fun ArrayList<ExecutableCommandData>.media(command: ExecutableCommand<*>) {
        add(ExecutableCommandData(QueueType.MEDIA, command))
    }

    private fun ArrayList<ExecutableCommandData>.showMessage(phraseId: String, data: MessageData) {
        sync(factoryProvider.commands.addMessage(phraseId, data))
    }
}