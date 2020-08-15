package com.boutiquesworld.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductDetailImageItemBinding

class ProductDetailImageAdapter :
    ListAdapter<String?, ProductDetailImageAdapter.ProductDetailViewHolder>(ProductDetailDiffUtil) {

    object ProductDetailDiffUtil : DiffUtil.ItemCallback<String?>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    class ProductDetailViewHolder(private val binding: ProductDetailImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String?) {
            binding.run {
                this.imageUrl = imageUrl
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        return ProductDetailViewHolder(
            ProductDetailImageItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) =
        holder.bind(getItem(position))
}