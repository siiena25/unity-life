package ru.mail.search.assistant.media.datasource.mutation

import com.google.android.exoplayer2.upstream.DataSource

class MutatingDataSourceFactory(
    private val upstreamFactory: DataSource.Factory,
    private val specMutation: SpecMutation? = null
) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        return MutatingDataSource(upstreamFactory.createDataSource(), specMutation)
    }
}