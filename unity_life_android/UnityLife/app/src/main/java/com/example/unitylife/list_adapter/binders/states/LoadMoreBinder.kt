package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemLoadingMore

class LoadMoreBinder : FeedItemViewBinder<ItemLoadingMore, LoadMoreBinder.LoadMoreHolder>(
        ItemLoadingMore::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return LoadMoreHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.footer_loading, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemLoadingMore, viewHolder: LoadMoreHolder) {
    }

    override fun getFeedItemType(): Int = R.layout.footer_loading

    override fun areItemsTheSame(oldItem: ItemLoadingMore, newItem: ItemLoadingMore): Boolean = false

    override fun areContentsTheSame(oldItem: ItemLoadingMore, newItem: ItemLoadingMore): Boolean = false

    inner class LoadMoreHolder(
            val view:View
    ): RecyclerView.ViewHolder(view)
}