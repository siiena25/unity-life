package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

/*
    volume: 50, # Громкость
    repeat: 0, # Зациклен ли плейлист
    random: 0, # Случайный порядок треков
    consume: 0, # "Съедать" (удалять) трек после проигрывания
    state: "play", # Состояние "play", "pause", "stop"
    audio: ["http://89.208.99.16:8088/zvezda_128"], # Очередь воспроизведения (массив)
    updated: 1540562700.5593095, # Время обновления информации
    song: 0, # Проигрываемый трек (индекс в массиве audio), может отсутствовать
    elapsed: 0.487, # Текущая позиция в треке в секундах
    duration: 0, # Длительность трека в секундах, 0 - если неизвестна
    is_playing: 1, # Играется ли сейчас очередь воспроизведения
    created: 1540562700.459477, # Время переключения на текущий трек
    skill: "radio" # Текущий "активный" скилл (создавший очередь воспроизведения)
 */

data class ApiTrackPlayerState(
    @SerializedName("volume") val volume: Int = 50,
    @SerializedName("repeat") val repeat: Int = 0,
    @SerializedName("random") val random: Int = 0,
    @SerializedName("consume") val consume: Int = 0,
    @SerializedName("state") val state: String,
    @SerializedName("audio") val audio: List<ApiTrack>,
    @SerializedName("song") val song: Int?,
    @SerializedName("elapsed") val elapsed: Long,
    @SerializedName("duration") val duration: Long,
    @SerializedName("is_playing") val isPlaying: Int,
    @SerializedName("created") val created: Float? = null,
    @SerializedName("updated") val updated: Float? = null,
    @SerializedName("skill") val skill: String
)


