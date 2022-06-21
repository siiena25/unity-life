package ru.mail.search.assistant.data.local

import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class TimeBasedMessageUuidProvider(private val logger: Logger? = null) : MessageUuidProvider {

    private var ident = AtomicLong()

    @Volatile
    private var uuid: UUID = generateUuid(System.currentTimeMillis())

    @Synchronized
    override fun get(): UUID {
        val lastUuid = uuid
        uuid = generateUuid(System.currentTimeMillis())

        if (uuid == lastUuid) {
            ident.incrementAndGet()
            logger?.e(Tag.ASSISTANT_MESSAGES, IllegalStateException("UUID collision detected"))
            return get()
        }

        return lastUuid
    }

    /**
     * Time-based UUID version 1
     * Any Long could be transformed in time based uuid and back
     * https://tools.ietf.org/html/rfc4122#section-4.1.2
     */
    fun generateUuid(timestamp: Long, hostIdent: Long = ident.get()): UUID {
        val clockHi = timestamp ushr 32
        val clockLo = timestamp
        var midhi = clockHi shl 16 or (clockHi ushr 16)
        midhi = midhi or 0x1000 // type 1
        val midhiL = (midhi shl 32) ushr 32
        val lsb = clockLo shl 32 or midhiL
        return UUID(lsb, hostIdent)
    }
}