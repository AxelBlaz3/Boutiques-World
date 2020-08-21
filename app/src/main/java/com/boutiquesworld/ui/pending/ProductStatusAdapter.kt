package com.boutiquesworld.ui.pending

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductStatusItemBinding
import com.boutiquesworld.model.BaseProduct

class ProductStatusAdapter :
    ListAdapter<BaseProduct.Product, ProductStatusAdapter.ProductStatusViewHolder>(
        ProductStatusDiffUtil
    ) {

    object ProductStatusDiffUtil : DiffUtil.ItemCallback<BaseProduct.Product>() {
        override fun areItemsTheSame(
            oldItem: BaseProduct.Product,
            newItem: BaseProduct.Product
        ): Boolean = oldItem.productId == newItem.productId

        override fun areContentsTheSame(
            oldItem: BaseProduct.Product,
            newItem: BaseProduct.Product
        ): Boolean =
            oldItem.businessId == newItem.businessId &&
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
                    oldItem.productImage4 == newItem.productImage4 &&
                    oldItem.productImage5 == newItem.productImage5 &&
                    oldItem.productThumb == newItem.productThumb

    }

    class ProductStatusViewHolder(private val binding: ProductStatusItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: BaseProduct.Product) {
            binding.run {
                this.product = product
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductStatusViewHolder =
        ProductStatusViewHolder(
            ProductStatusItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductStatusViewHolder, position: Int) =
        holder.bind(getItem(position))
}