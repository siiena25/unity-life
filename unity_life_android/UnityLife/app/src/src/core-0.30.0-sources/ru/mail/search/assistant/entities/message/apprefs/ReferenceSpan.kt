package ru.mail.search.assistant.entities.message.apprefs

import android.text.style.ClickableSpan
import android.view.View

class ReferenceSpan<T>(val payload: T, val start: Int, val end: Int) : ClickableSpan() {

    private var clickListener: (() -> Unit)? = null

    override fun onClick(widget: View) {
        clickListener?.invoke()
    }

    fun setClickListener(listener: () -> Unit) {
        clickListener = listener
    }
}