package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.EmptyNewsListBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemEmptyList

class EmptyNewsListBinder : FeedItemViewBinder<ItemEmptyList, EmptyNewsListBinder.EmptyListHolder>(
    ItemEmptyList::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.empty_news_list, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemEmptyList, viewHolder: EmptyListHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.empty_news_list

    override fun areItemsTheSame(oldItem: ItemEmptyList, newItem: ItemEmptyList): Boolean = false

    override fun areContentsTheSame(oldItem: ItemEmptyList, newItem: ItemEmptyList): Boolean = false

    inner class EmptyListHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding = EmptyNewsListBinding.bind(view)

        fun bind(data: ItemEmptyList) {
            val icon = ContextCompat.getDrawable(view.context, R.drawable.no_promo_news)
            Glide.with(view).load(icon).dontTransform().into(binding.emptyListIcon)
            binding.emptyListTvText.setText(data.textId)
        }
    }
}