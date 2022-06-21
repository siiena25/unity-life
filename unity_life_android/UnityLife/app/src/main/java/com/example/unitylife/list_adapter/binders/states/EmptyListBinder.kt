package com.llc.aceplace_ru.list_adapter.binders.states

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.llc.aceplace_ru.R
import com.llc.aceplace_ru.databinding.EmptyListBinding
import com.llc.aceplace_ru.list_adapter.FeedItemViewBinder
import com.llc.aceplace_ru.list_adapter.models.ItemEmptyList
import com.llc.aceplace_ru.ui.base.ContainerLoginActivity

class
EmptyListBinder : FeedItemViewBinder<ItemEmptyList, EmptyListBinder.EmptyListHolder>(
        ItemEmptyList::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyListHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.empty_list, parent, false)
        )
    }

    override fun bindViewHolder(model: ItemEmptyList, viewHolder: EmptyListHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.empty_list

    override fun areItemsTheSame(oldItem: ItemEmptyList, newItem: ItemEmptyList): Boolean = false

    override fun areContentsTheSame(oldItem: ItemEmptyList, newItem: ItemEmptyList): Boolean = false

    inner class EmptyListHolder(
            val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val binding = EmptyListBinding.bind(view)

        fun bind(data: ItemEmptyList) {
            val icon = ContextCompat.getDrawable(view.context, data.drawableId)
            Glide.with(view).load(icon).dontTransform().into(binding.emptyListIcon)
            binding.emptyListTvText.setText(data.textId)
            data.btnTextId.let { binding.emptyListBtnAction.setText(it) }
            binding.emptyListBtnAction.setOnClickListener { view ->
                when {
                    data.btnTextId == R.string.sign_up_text -> authorizationAction(view)
                    data.textId == R.string.empty_news -> view.findNavController().navigate(R.id.newsListFragment)
                    data.textId == R.string.empty_notifications -> view.findNavController().navigate(R.id.notificationsFragment)
                    else -> view.findNavController().navigate(R.id.mapsFragment)
                }
            }
        }

        private fun authorizationAction(view: View) {
            val newIntent = Intent(view.context, ContainerLoginActivity::class.java)
            view.context.startActivity(newIntent)
        }
    }
}