package com.boutiquesworld.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.CartItemBinding
import com.boutiquesworld.model.Cart

/**
 * Adapter for Cart.
 */
class CartAdapter(private val listener: CartAdapterListener) :
    ListAdapter<Cart, CartAdapter.CartViewHolder>(CartDiffUtil) {

    interface CartAdapterListener {
        fun onAddButtonClick(quantityView: TextView, priceView: TextView, cart: Cart)
        fun onRemoveButtonClick(quantityView: TextView, priceView: TextView, cart: Cart)
        fun onDeleteButtonClick(cart: Cart)
    }

    object CartDiffUtil : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem.businessId == newItem.businessId &&
                    oldItem.productCategory == newItem.productCategory &&
                    oldItem.productImage == newItem.productImage &&
                    oldItem.productId == newItem.productId &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productPrice == newItem.productPrice &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.userId == newItem.userId &&
                    oldItem.productType == newItem.productType
    }

    class CartViewHolder(
        private val binding: CartItemBinding,
        private val listener: CartAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart) {
            binding.run {
                this.cart = cart
                this.listener = this@CartViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
        CartViewHolder(
            CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) =
        holder.bind(getItem(position))
}