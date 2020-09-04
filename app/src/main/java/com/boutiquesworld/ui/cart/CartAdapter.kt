package com.boutiquesworld.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
        fun onDeleteButtonClick(cart: Cart)
        fun onCartItemUpdated(newCartItem: Cart)
        fun onCartListsDiffer(areDifferent: Boolean)
    }

    object CartDiffUtil : DiffUtil.ItemCallback<Cart>() {
        var cartListCount = 0
        var listener: CartAdapterListener? = null
        var i = 0
        private var areTwoListsSame = true

        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            val areContentsTheSame = oldItem.businessId == newItem.businessId &&
                    oldItem.productCategory == newItem.productCategory &&
                    oldItem.productImage == newItem.productImage &&
                    oldItem.productId == newItem.productId &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productDescription == newItem.productDescription &&
                    oldItem.productPrice == newItem.productPrice &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.maxQuantity == newItem.maxQuantity &&
                    oldItem.userId == newItem.userId &&
                    oldItem.userCategory == newItem.userCategory &&
                    oldItem.productType == newItem.productType

            areTwoListsSame = areTwoListsSame && areContentsTheSame
            if (++i == cartListCount) {
                listener?.onCartListsDiffer(!areTwoListsSame)
                i = 0
                areTwoListsSame = true
            }
            return areContentsTheSame
        }
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
                    if (cart.maxQuantity < cart.quantity)
                        cart.quantity = cart.maxQuantity
                    setText(cart.quantity.toString(), false)
                    onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, position, _ ->
                            updateQuantityAndPrice(binding, cart, adapter.getItem(position) as Int)
                        }
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
            (newQuantity * productPrice.toInt() / quantity).toString()

        private fun updateQuantityAndPrice(binding: CartItemBinding, cart: Cart, newQuantity: Int) {
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
