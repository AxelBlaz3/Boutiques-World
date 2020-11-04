package `in`.trendition.ui.profile

import `in`.trendition.databinding.OrderItemBinding
import `in`.trendition.model.Payment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PaymentsAdapter :
    ListAdapter<Payment, PaymentsAdapter.PaymentsDiffUtil.PaymentsViewHolder>(PaymentsDiffUtil) {

    object PaymentsDiffUtil : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem.businessId == newItem.businessId &&
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
                    oldItem.productType == newItem.productType &&
                    oldItem.paymentStatus == newItem.paymentStatus

        class PaymentsViewHolder(private val binding: OrderItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(payment: Payment) {
                binding.run {
                    this.payment = payment
                    executePendingBindings()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentsDiffUtil.PaymentsViewHolder =
        PaymentsDiffUtil.PaymentsViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PaymentsDiffUtil.PaymentsViewHolder, position: Int) =
        holder.bind(getItem(position))
}