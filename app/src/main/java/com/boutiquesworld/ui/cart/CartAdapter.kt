package com.boutiquesworld.ui.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.CartItemBinding
import com.boutiquesworld.model.Cart
import com.google.android.material.internal.TextWatcherAdapter

/**
 * Adapter for Cart.
 */
class CartAdapter(private val listener: CartAdapterListener) :
    ListAdapter<Cart, CartAdapter.CartViewHolder>(CartDiffUtil) {

    interface CartAdapterListener {
        fun onDeleteButtonClick(cart: Cart)
        fun onCartItemUpdated(newCartItem: Cart)
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
                    oldItem.productDescription == newItem.productDescription &&
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
                cartProductQuantity.run {
                    setText(cart.quantity.toString(), false)
                    addTextChangedListener(object : TextWatcherAdapter() {

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            updateQuantityAndPrice(binding, cart, s.toString().toInt())
                        }
                    })
                    setAdapter(
                        ArrayAdapter(
                            binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            (1..cart.maxQuantity).toList()
                        )
                    )
                }
                executePendingBindings()
            }
        }

        private fun getPrice(quantity: Int, productPrice: String, newQuantity: Int): String =
            String.format("%.2f", newQuantity * (productPrice.toFloat() / quantity))

        private fun updateQuantityAndPrice(binding: CartItemBinding, cart: Cart, newQuantity: Int) {
            Log.d(this.javaClass.simpleName, cart.productName)
            binding.run {
                val newCart = cart.copy(
                    productPrice = getPrice(
                        this.cart!!.quantity,
                        this.cart!!.productPrice,
                        newQuantity
                    ), quantity = newQuantity
                )
                this.cart = newCart
                this@CartViewHolder.listener.onCartItemUpdated(newCart)
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