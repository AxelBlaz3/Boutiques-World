package `in`.trendition.ui.order

import `in`.trendition.databinding.MyOrderItemBinding
import `in`.trendition.model.Order
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for Cart.
 */
class MyOrdersAdapter(private val listener: OrderAdapter.OrderAdapterListener) :
    ListAdapter<Order, MyOrdersAdapter.MyOrdersViewHolder>(OrderAdapter.OrderDiffUtil) {

    class MyOrdersViewHolder(
        private val binding: MyOrderItemBinding,
        private val listener: OrderAdapter.OrderAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.run {
                this.order = order
                this.listener = this@MyOrdersViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder =
        MyOrdersViewHolder(
            MyOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) =
        holder.bind(getItem(position))
}
