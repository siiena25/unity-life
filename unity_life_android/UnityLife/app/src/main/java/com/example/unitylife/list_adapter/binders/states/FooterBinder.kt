package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemFooter

class FooterBinder :
    FeedItemViewBinder<ItemFooter, FooterBinder.FooterHolder>(ItemFooter::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            getFeedItemType(),
            parent,
            false
        )
        return FooterHolder(view)
    }

    override fun bindViewHolder(model: ItemFooter, viewHolder: FooterHolder) {
    }

    override fun getFeedItemType(): Int {
        return R.layout.item_footer
    }

    override fun areItemsTheSame(oldItem: ItemFooter, newItem: ItemFooter): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: ItemFooter, newItem: ItemFooter): Boolean {
        return oldItem == newItem
    }

    inner class FooterHolder(view: View) : RecyclerView.ViewHolder(view)
}