package com.boutiquesworld.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.OrderItemBinding
import com.boutiquesworld.model.Order

/**
 * Adapter for Cart.
 */
class OrderAdapter(private val listener: OrderAdapterListener) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffUtil) {

    interface OrderAdapterListener {
        fun onOrderItemClicked(order: Order)
    }

    object OrderDiffUtil : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.businessId == newItem.businessId &&
                    oldItem.productCategory == newItem.productCategory &&
                    oldItem.productImage == newItem.productImage &&
                    oldItem.productId == newItem.productId &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productDescription == newItem.productDescription &&
                    oldItem.productPrice == newItem.productPrice &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.availableQuantity == newItem.availableQuantity &&
                    oldItem.userId == newItem.userId &&
                    oldItem.userCategory == newItem.userCategory &&
                    oldItem.productType == newItem.productType
        }
    }

    class OrderViewHolder(
        private val binding: OrderItemBinding,
        private val listener: OrderAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.run {
                this.order = order
                this.listener = this@OrderViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder =
        OrderViewHolder(
            OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) =
        holder.bind(getItem(position))
}
