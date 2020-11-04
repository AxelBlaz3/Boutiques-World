package `in`.trendition.ui.profile

import `in`.trendition.databinding.AboutItemBinding
import `in`.trendition.model.About
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class AboutAdapter :
    androidx.recyclerview.widget.ListAdapter<About, AboutViewHolder>(AboutDiffUtil) {

    object AboutDiffUtil : DiffUtil.ItemCallback<About>() {
        override fun areItemsTheSame(oldItem: About, newItem: About): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: About, newItem: About): Boolean =
            oldItem.detail == newItem.detail && oldItem.icon == newItem.icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder =
        AboutViewHolder(
            AboutItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}

class AboutViewHolder(private val binding: AboutItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(aboutItem: About, position: Int) {
        binding.run {
            this.about = aboutItem
            this.position = position
            executePendingBindings()
        }
    }
}