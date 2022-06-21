package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.ItemHeaderBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemHeader

class HeaderViewBinder : FeedItemViewBinder<ItemHeader, HeaderViewBinder.HeaderHolder>(ItemHeader::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
        return HeaderHolder(view)
    }

    override fun bindViewHolder(model: ItemHeader, viewHolder: HeaderHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.item_header

    override fun areItemsTheSame(oldItem: ItemHeader, newItem: ItemHeader): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: ItemHeader, newItem: ItemHeader): Boolean = oldItem.resId == newItem.resId

    inner class HeaderHolder(
            view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemHeaderBinding.bind(view)

        fun bind(item: ItemHeader) {
            binding.itemHeaderTvTitle.setText(item.resId)
        }
    }
}