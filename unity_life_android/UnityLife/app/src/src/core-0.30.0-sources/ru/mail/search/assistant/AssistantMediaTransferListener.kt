package ru.mail.search.assistant

import android.net.Uri

interface AssistantMediaTransferListener {

    fun onTransferInitializing(uri: Uri?, isNetwork: Boolean)

    fun onTransferStart(uri: Uri?, isNetwork: Boolean)

    fun onBytesTransferred(uri: Uri?, isNetwork: Boolean, bytesTransferred: Int)

    fun onTransferEnd(uri: Uri?, isNetwork: Boolean)
}