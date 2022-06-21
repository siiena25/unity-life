package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.photo.ImageDataPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.photo.ImagePayload
import ru.mail.search.assistant.data.local.messages.converter.payload.photo.PhotoAlbumPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.photo.ThumbnailPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.images.Image
import ru.mail.search.assistant.entities.message.images.ImageData
import ru.mail.search.assistant.entities.message.images.Thumbnail

internal class PhotoAlbumPayloadConverter :
    PayloadGsonConverter<MessageData.PhotoAlbum, PhotoAlbumPayload>() {

    override val type: String get() = MessageTypes.PHOTO_ALBUM

    override fun payloadToPojo(payload: String): MessageData.PhotoAlbum {
        return fromJson<PhotoAlbumPayload>(payload) {
            MessageData.PhotoAlbum(
                imageData = imageData.map { imageData ->
                    ImageData(
                        image = imageData.image?.let { image ->
                            Image(
                                url = image.url,
                                width = image.width,
                                height = image.height,
                                ext = image.ext,
                                size = image.size
                            )
                        },
                        thumbnail = imageData.thumbnail?.let { thumbnail ->
                            Thumbnail(
                                url = thumbnail.url,
                                width = thumbnail.width,
                                height = thumbnail.height
                            )
                        }
                    )
                },
                morePhotosUrl = morePhotosUrl
            )
        }
    }

    override fun pojoToPayload(data: MessageData.PhotoAlbum): String {
        return toJson(data) {
            PhotoAlbumPayload(
                imageData = imageData.map { imageData ->
                    ImageDataPayload(
                        image = imageData.image?.let { image ->
                            ImagePayload(
                                url = image.url,
                                width = image.width,
                                height = image.height,
                                ext = image.ext,
                                size = image.size
                            )
                        },
                        thumbnail = imageData.thumbnail?.let { thumbnail ->
                            ThumbnailPayload(
                                url = thumbnail.url,
                                width = thumbnail.width,
                                height = thumbnail.height
                            )
                        }
                    )
                },
                morePhotosUrl = morePhotosUrl
            )
        }
    }
}