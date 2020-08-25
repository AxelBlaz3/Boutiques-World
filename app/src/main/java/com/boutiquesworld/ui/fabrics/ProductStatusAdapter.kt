package com.boutiquesworld.ui.fabrics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductStatusItemBinding
import com.boutiquesworld.model.BaseProduct
import com.google.android.material.textfield.TextInputEditText

class ProductStatusAdapter(
    private val listener: ProductStatusAdapterListener
) :
    ListAdapter<BaseProduct.Fabric, ProductStatusAdapter.ProductViewHolder>(FabricDiffUtil) {

    interface ProductStatusAdapterListener {
        fun onSaveButtonClick(productId: String, productQuantityEditText: TextInputEditText)
    }

    class ProductViewHolder(
        private val binding: ProductStatusItemBinding,
        private val listener: ProductStatusAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fabric: BaseProduct.Fabric, position: Int) {
            binding.run {
                this.fabric = fabric
                this.position = position
                this.listener = this@ProductViewHolder.listener
                executePendingBindings()
            }
        }
    }

    object FabricDiffUtil : DiffUtil.ItemCallback<BaseProduct.Fabric>() {
        override fun areItemsTheSame(
            oldItem: BaseProduct.Fabric,
            newItem: BaseProduct.Fabric
        ): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(
            oldItem: BaseProduct.Fabric,
            newItem: BaseProduct.Fabric
        ): Boolean {
            return oldItem.businessId == newItem.businessId &&
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
            ProductStatusItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}