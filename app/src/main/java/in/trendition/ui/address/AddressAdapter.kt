package `in`.trendition.ui.address

import `in`.trendition.databinding.AddressItemBinding
import `in`.trendition.model.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(
    private val listener: AddressAdapterListener,
    private val addressSelectionIndices: List<Int>
) :
    ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffUtil) {

    interface AddressAdapterListener {
        fun onAddressCardClick(position: Int)
    }

    object AddressDiffUtil : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem.fullName == newItem.fullName &&
                    oldItem.mobile == newItem.mobile &&
                    oldItem.pincode == newItem.pincode &&
                    oldItem.flat == newItem.flat &&
                    oldItem.area == newItem.area &&
                    oldItem.landmark == newItem.landmark &&
                    oldItem.city == newItem.city &&
                    oldItem.state == newItem.state &&
                    oldItem.orderId == newItem.orderId
    }

    class AddressViewHolder(
        private val binding: AddressItemBinding,
        private val listener: AddressAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address, position: Int, shouldCheck: Boolean) {
            binding.run {
                this.position = position
                this.address = address.copy(isChecked = shouldCheck)
                this.listener = this@AddressViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder =
        AddressViewHolder(
            AddressItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) =
        holder.bind(getItem(position), position, addressSelectionIndices[0] == position)
}