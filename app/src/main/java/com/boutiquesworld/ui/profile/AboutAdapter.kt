package com.boutiquesworld.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.AboutItemBinding

class AboutAdapter() :
    androidx.recyclerview.widget.ListAdapter<String, AboutViewHolder>(AboutDiffUtil) {

    object AboutDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder =
        AboutViewHolder(
            AboutItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}

class AboutViewHolder(private val binding: AboutItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(content: String, position: Int) {
        binding.run {
            this.content = content
            this.position = position
            executePendingBindings()
        }
    }
}