package com.llc.aceplace_ru.list_adapter.binders.states

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.ErrorScreenBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemFailureList

class FailureListBinder(
        private val block: (data: ItemFailureList) -> Unit
) : FeedItemViewBinder<ItemFailureList, FailureListBinder.FailureHolder>(ItemFailureList::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return FailureHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.error_screen, parent, false),
                block
        )
    }

    override fun bindViewHolder(item: ItemFailureList, viewHolder: FailureHolder) {
        viewHolder.bind(item)
    }

    override fun getFeedItemType(): Int = R.layout.error_screen

    override fun areItemsTheSame(oldItem: ItemFailureList, newItem: ItemFailureList): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemFailureList, newItem: ItemFailureList): Boolean {
        return oldItem == newItem
    }

    inner class FailureHolder(
            val view: View,
            private val block: (data: ItemFailureList) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private var binding: ErrorScreenBinding = ErrorScreenBinding.bind(view)

        fun bind(item: ItemFailureList) {
            item.textId?.let {resId -> binding.errorScreenTvText.setText(resId) }
            binding.errorScreenBtnRetry.setOnClickListener { block(item) }
        }
    }
}