package com.boutiquesworld.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.SpecificationItemBinding

class SpecificationAdapter :
    ListAdapter<Pair<String, String>, SpecificationAdapter.SpecificationViewHolder>(
        SpecificationDiffUtil
    ) {

    object SpecificationDiffUtil : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean = oldItem.second == newItem.second
    }

    class SpecificationViewHolder(private val binding: SpecificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(specification: Pair<String, String>) {
            binding.run {
                this.specification = specification
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificationViewHolder =
        SpecificationViewHolder(
            SpecificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SpecificationViewHolder, position: Int) =
        holder.bind(getItem(position))
}