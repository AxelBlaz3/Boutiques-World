package com.boutiquesworld.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.ProductStatusItemBinding
import com.boutiquesworld.model.BaseProduct
import com.google.android.material.textfield.TextInputEditText

class ProductStatusAdapter(
    private val listener: ProductStatusAdapterListener
) :
    ListAdapter<BaseProduct.Store, ProductStatusAdapter.ProductViewHolder>(StoreDiffUtil) {

    interface ProductStatusAdapterListener {
        fun onSaveButtonClick(store: BaseProduct.Store, productQuantityTextView: TextView)
        fun onMinusClicked(productQuantityTextView: TextView)
        fun onPlusClicked(productQuantityTextView: TextView)
    }

    class ProductViewHolder(
        private val binding: ProductStatusItemBinding,
        private val listener: ProductStatusAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(store: BaseProduct.Store, position: Int) {
            binding.run {
                add.isEnabled = store.productStatus == 1
                remove.isEnabled = store.productStatus == 1
                this.store = store
                this.position = position
                this.listener = this@ProductViewHolder.listener
                executePendingBindings()
            }
        }
    }

    object StoreDiffUtil : DiffUtil.ItemCallback<BaseProduct.Store>() {
        override fun areItemsTheSame(
            oldItem: BaseProduct.Store,
            newItem: BaseProduct.Store
        ): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(
            oldItem: BaseProduct.Store,
            newItem: BaseProduct.Store
        ): Boolean =
                oldItem.availableQuantity == newItem.availableQuantity &&
                        oldItem.businessCategory == newItem.businessCategory &&
                        oldItem.businessCategoryType == newItem.businessCategoryType &&
                        oldItem.productCategory == newItem.productCategory &&
                        oldItem.productThumb == newItem.productThumb &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.productType == newItem.productType &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.businessId == newItem.businessId &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.upid == newItem.upid &&
                        oldItem.likes == newItem.likes &&
                        oldItem.date == newItem.date &&
                        oldItem.productStatus == newItem.productStatus
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