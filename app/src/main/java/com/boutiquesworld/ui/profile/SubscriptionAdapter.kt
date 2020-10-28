package com.boutiquesworld.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.SubscriptionItemBinding
import com.boutiquesworld.model.Subscription

class SubscriptionAdapter :
    ListAdapter<Subscription, SubscriptionAdapter.SubscriptionViewHolder>(SubscriptionDiffUtil) {

    object SubscriptionDiffUtil : DiffUtil.ItemCallback<Subscription>() {
        override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean =
            oldItem.amount == newItem.amount &&
                    oldItem.businessId == newItem.amount &&
                    oldItem.businessName == newItem.businessName &&
                    oldItem.endDate == newItem.endDate &&
                    oldItem.paidDate == newItem.paidDate &&
                    oldItem.uuid == newItem.uuid

    }

    class SubscriptionViewHolder(private val binding: SubscriptionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            binding.run {
                this.subscription = subscription
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder =
        SubscriptionViewHolder(
            SubscriptionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) =
        holder.bind(getItem(position))
}