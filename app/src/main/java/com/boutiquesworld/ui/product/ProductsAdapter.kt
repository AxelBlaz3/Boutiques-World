package com.boutiquesworld.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductItemBinding
import com.boutiquesworld.model.Product

class ProductsAdapter(private val listener: ProductsAdapterListener) :
    ListAdapter<Product, ProductsAdapter.ProductViewHolder>(ProductsDiffUtil) {

    interface ProductsAdapterListener {
        fun onProductClick(product: Product, position: Int)
    }

    class ProductViewHolder(
        private val binding: ProductItemBinding,
        private val listener: ProductsAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, position: Int) {
            binding.run {
                this.product = product
                this.position = position
                listener = this@ProductViewHolder.listener
                executePendingBindings()
            }
        }
    }

    object ProductsDiffUtil : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.businessId == newItem.businessId &&
                    oldItem.businessName == newItem.businessName &&
                    oldItem.date == newItem.date &&
                    oldItem.endPrice == newItem.endPrice &&
                    oldItem.startPrice == newItem.startPrice &&
                    oldItem.likes == newItem.likes &&
                    oldItem.productCloth == newItem.productCloth &&
                    oldItem.preparationTime == newItem.preparationTime &&
                    oldItem.productColor == newItem.productColor &&
                    oldItem.productDescription == newItem.productDescription &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productFabric == newItem.productFabric &&
                    oldItem.productOccasion == newItem.productOccasion &&
                    oldItem.zone == newItem.zone &&
                    oldItem.upid == newItem.upid &&
                    oldItem.uuid == newItem.uuid &&
                    oldItem.productImage1 == newItem.productImage1 &&
                    oldItem.productImage2 == newItem.productImage2 &&
                    oldItem.productImage3 == newItem.productImage3 &&
                    oldItem.productThumb == newItem.productThumb
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}