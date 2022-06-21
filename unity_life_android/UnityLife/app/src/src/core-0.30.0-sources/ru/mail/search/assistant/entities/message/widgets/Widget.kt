package ru.mail.search.assistant.entities.message.widgets

sealed class Widget {

    abstract val id: Long

    class Common(
        override val id: Long,
        val data: WidgetData
    ) : Widget()

    data class ListWidget(
        override val id: Long,
        val data: WidgetData,
        val isSuccess: Boolean,
        val useCheckbox: Boolean,
        val lines: Int,
        val items: List<ListWidgetItem>,
        val emptyState: ListWidgetEmptyState?
    ) : Widget()
}