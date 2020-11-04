package `in`.trendition.ui.product

import `in`.trendition.databinding.SizeItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Singleton

@Singleton
class ProductSizeAdapter(private val listener: ProductSizeAdapterListener) :
    ListAdapter<String, ProductSizeAdapter.ProductSizeViewHolder>(ProductSizeDiffUtil) {

    interface ProductSizeAdapterListener {
        fun onSizeClicked(size: String)
    }

    object ProductSizeDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    class ProductSizeViewHolder(private val binding: SizeItemBinding, private val listener: ProductSizeAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(size: String) {
            binding.run {
                this.size = size
                this.listener = this@ProductSizeViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSizeViewHolder =
        ProductSizeViewHolder(SizeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)

    override fun onBindViewHolder(holder: ProductSizeViewHolder, position: Int) =
        holder.bind(getItem(position))
}