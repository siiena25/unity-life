package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.FooterEmptyBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemNoMore

class NoMoreBinder : FeedItemViewBinder<ItemNoMore, NoMoreBinder.NoMoreHolder>(
        ItemNoMore::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NoMoreHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.footer_empty, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemNoMore, viewHolder: NoMoreHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.footer_empty

    override fun areItemsTheSame(oldItem: ItemNoMore, newItem: ItemNoMore): Boolean = false

    override fun areContentsTheSame(oldItem: ItemNoMore, newItem: ItemNoMore): Boolean = false

    inner class NoMoreHolder(
            val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding: FooterEmptyBinding = FooterEmptyBinding.bind(view)

        fun bind(data: ItemNoMore) {
            binding.footerEmptyTvText.setText(data.textId)
        }
    }
}