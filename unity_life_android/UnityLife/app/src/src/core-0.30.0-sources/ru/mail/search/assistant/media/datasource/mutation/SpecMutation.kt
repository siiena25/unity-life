package ru.mail.search.assistant.media.datasource.mutation

import com.google.android.exoplayer2.upstream.DataSpec

abstract class SpecMutation {

    open fun mutate(dataSpec: DataSpec): DataSpec {
        return DataSpec.Builder()
            .setUri(dataSpec.uri)
            .setHttpMethod(dataSpec.httpMethod)
            .setHttpBody(dataSpec.httpBody)
            .setPosition(dataSpec.position)
            .setUriPositionOffset(dataSpec.uriPositionOffset)
            .setLength(dataSpec.length)
            .setKey(dataSpec.key)
            .setFlags(getFlags(dataSpec))
            .build()
    }

    protected open fun getFlags(dataSpec: DataSpec): Int {
        return dataSpec.flags
    }
}