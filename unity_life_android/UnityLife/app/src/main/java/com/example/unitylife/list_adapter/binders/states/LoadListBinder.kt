package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemLoadingList

class LoadListBinder : FeedItemViewBinder<ItemLoadingList, LoadListBinder.LoadListHolder>(ItemLoadingList::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LoadListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_screen, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemLoadingList, viewHolder: LoadListHolder) {}

    override fun getFeedItemType(): Int = R.layout.loading_screen

    override fun areItemsTheSame(oldItem: ItemLoadingList, newItem: ItemLoadingList): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: ItemLoadingList, newItem: ItemLoadingList): Boolean = oldItem.loadingId == newItem.loadingId

    inner class LoadListHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view)
}