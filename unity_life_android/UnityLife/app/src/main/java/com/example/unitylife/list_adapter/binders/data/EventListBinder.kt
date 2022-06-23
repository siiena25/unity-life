package com.example.unitylife.list_adapter.binders.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.unitylife.R
import com.example.unitylife.data.models.EventModel
import com.example.unitylife.databinding.ItemEventBinding
import com.example.unitylife.ext.toUrl
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.example.unitylife.utils.TimeUtils.getDateTimeFromTimestamp

class EventListBinder(
    private val block: (data: EventModel) -> Unit,
    private val requestManager: RequestManager,
) : FeedItemViewBinder<EventModel, EventListBinder.VhPromo>(EventModel::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return VhPromo(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false),
            block
        )
    }

    override fun bindViewHolder(model: EventModel, viewHolder: VhPromo) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.item_event

    override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
        return oldItem.authorId == newItem.authorId
                && oldItem.eventId == newItem.eventId
                && oldItem.eventAvatar == newItem.eventAvatar
                && oldItem.title == newItem.title
                && oldItem.address == newItem.address
                && oldItem.categoryTitle == newItem.categoryTitle
                && oldItem.address == newItem.address
                && oldItem.description == newItem.description
                && oldItem.categoryTitle == newItem.categoryTitle
    }

    override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
        return oldItem == newItem
    }

    inner class VhPromo(
        val view: View,
        val block: (data: EventModel) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemEventBinding = ItemEventBinding.bind(view)
        private val logoHolder =
            ContextCompat.getDrawable(view.context, R.drawable.empty_cover)

        fun bind(item: EventModel) {
            itemView.setOnClickListener { block(item) }

            binding.apply {
                eventTitle.text = item.title
                eventAddress.text = item.address
                eventCategoryTitle.text = item.categoryTitle
                eventDuration.text = getEventDuration(item.timestart, item.timeend)
                requestManager.load(item.eventAvatar)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(logoHolder)
                    .override(320)
                    .into(eventImage)
            }
        }
    }

    private fun getEventDuration(timestart: String, timeend: String): CharSequence {
        return getDateTimeFromTimestamp(timestart) + " - " + getDateTimeFromTimestamp(timeend)
    }
}