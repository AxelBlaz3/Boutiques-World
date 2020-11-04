package `in`.trendition.ui.requests

import `in`.trendition.databinding.RequestItemBinding
import `in`.trendition.model.BoutiqueRequest
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BoutiqueRequestAdapter(private val listener: BoutiqueRequestAdapterListener) :
    ListAdapter<BoutiqueRequest, BoutiqueRequestAdapter.BoutiqueRequestViewHolder>(
        BoutiqueRequestDiffUtil
    ) {

    interface BoutiqueRequestAdapterListener {
        fun onBoutiqueRequestClick(request: BoutiqueRequest)
    }

    object BoutiqueRequestDiffUtil : DiffUtil.ItemCallback<BoutiqueRequest>() {
        override fun areItemsTheSame(oldItem: BoutiqueRequest, newItem: BoutiqueRequest): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: BoutiqueRequest,
            newItem: BoutiqueRequest
        ): Boolean =
            oldItem.requestStatus == newItem.requestStatus &&
                    oldItem.email == newItem.email &&
                    oldItem.message == newItem.message &&
                    oldItem.mobile == newItem.mobile &&
                    oldItem.occasion == newItem.occasion &&
                    oldItem.size == newItem.size &&
                    oldItem.timePeriod == newItem.timePeriod &&
                    oldItem.typeOfDress == newItem.typeOfDress &&
                    oldItem.upload == newItem.upload &&
                    oldItem.userName == newItem.userName
    }

    class BoutiqueRequestViewHolder(
        private val binding: RequestItemBinding,
        private val listener: BoutiqueRequestAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: BoutiqueRequest) {
            binding.run {
                this.request = request
                this.listener = this@BoutiqueRequestViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoutiqueRequestViewHolder =
        BoutiqueRequestViewHolder(
            RequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )

    override fun onBindViewHolder(holder: BoutiqueRequestViewHolder, position: Int) =
        holder.bind(getItem(position))
}