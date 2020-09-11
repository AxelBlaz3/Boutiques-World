package com.boutiquesworld.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.SummaryProductItemBinding
import com.boutiquesworld.model.Cart
import com.boutiquesworld.ui.cart.CartAdapter

class OrderSummaryAdapter :
    ListAdapter<Cart, OrderSummaryAdapter.OrderSummaryViewHolder>(CartAdapter.CartDiffUtil) {

    class OrderSummaryViewHolder(private val binding: SummaryProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart) {
            binding.run {
                this.cart = cart
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder =
        OrderSummaryViewHolder(
            SummaryProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) =
        holder.bind(getItem(position))
}