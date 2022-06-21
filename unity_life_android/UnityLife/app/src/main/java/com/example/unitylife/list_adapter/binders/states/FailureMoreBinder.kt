package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.FooterErrorBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemFailureMore

class FailureMoreBinder(
        val block: () -> Unit
) : FeedItemViewBinder<ItemFailureMore, FailureMoreBinder.FailureMoreHolder>(
        ItemFailureMore::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return FailureMoreHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.footer_error, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemFailureMore, viewHolder: FailureMoreHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.footer_error

    override fun areItemsTheSame(oldItem: ItemFailureMore, newItem: ItemFailureMore): Boolean = false

    override fun areContentsTheSame(oldItem: ItemFailureMore, newItem: ItemFailureMore): Boolean = false

    inner class FailureMoreHolder(
            val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding = FooterErrorBinding.bind(view)

        fun bind(data: ItemFailureMore) {
            binding.footerErrorBtnRetry.setOnClickListener { block() }
            binding.footerErrorTvText.setText(data.textId)
        }
    }
}