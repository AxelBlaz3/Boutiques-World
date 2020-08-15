package com.boutiquesworld.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductItemBinding
import com.boutiquesworld.model.BaseProduct

class ProductsAdapter(
    private val listener: ProductsAdapterListener
) :
    ListAdapter<BaseProduct, ProductsAdapter.ProductViewHolder>(ProductDiffUtil) {

    interface ProductsAdapterListener {
        fun onProductClick(product: BaseProduct, position: Int)
    }

    class ProductViewHolder(
        private val binding: ProductItemBinding,
        private val listener: ProductsAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: BaseProduct, position: Int) {
            binding.run {
                if (product is BaseProduct.Product)
                    this.product = product
                else
                    this.fabric = product as BaseProduct.Fabric
                this.position = position
                isFabric = product is BaseProduct.Fabric
                listener = this@ProductViewHolder.listener
                executePendingBindings()
            }
        }
    }

    object ProductDiffUtil : DiffUtil.ItemCallback<BaseProduct>() {
        override fun areItemsTheSame(
            oldItem: BaseProduct,
            newItem: BaseProduct
        ): Boolean =
            if (oldItem is BaseProduct.Product && newItem is BaseProduct.Product)
                oldItem.productId == newItem.productId
            else
                (oldItem as BaseProduct.Fabric).productId == (newItem as BaseProduct.Fabric).productId

        override fun areContentsTheSame(
            oldItem: BaseProduct,
            newItem: BaseProduct
        ): Boolean {
            if (oldItem is BaseProduct.Product && newItem is BaseProduct.Product)
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
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.productThumb == newItem.productThumb
            else
                return (oldItem as BaseProduct.Fabric).businessId == (newItem as BaseProduct.Fabric).businessId &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.date == newItem.date &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.likes == newItem.likes &&
                        oldItem.productCloth == newItem.productCloth &&
                        oldItem.deliveryTime == newItem.deliveryTime &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productFabric == newItem.productFabric &&
                        oldItem.availableMeters == newItem.availableMeters &&
                        oldItem.upid == newItem.upid &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
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