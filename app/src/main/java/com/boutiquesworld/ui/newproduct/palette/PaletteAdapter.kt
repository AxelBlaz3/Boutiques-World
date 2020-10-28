package com.boutiquesworld.ui.newproduct.palette

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.databinding.PaletteItemBinding
import com.boutiquesworld.model.Palette
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class PaletteAdapter(private val listener: PaletteAdapterListener) :
    ListAdapter<Palette, PaletteAdapter.PaletteViewHolder>(PaletteDiffUtil) {

    interface PaletteAdapterListener {
        fun onColorClicked(palette: Palette)
    }

    object PaletteDiffUtil : DiffUtil.ItemCallback<Palette>() {
        override fun areItemsTheSame(oldItem: Palette, newItem: Palette): Boolean {
            return oldItem.hexColorTint == oldItem.hexColorTint
        }

        override fun areContentsTheSame(oldItem: Palette, newItem: Palette): Boolean {
            return oldItem.hexColorTint == newItem.hexColorTint && oldItem.colorName == newItem.colorName
        }
    }

    class PaletteViewHolder(
        private val binding: PaletteItemBinding,
        private val listener: PaletteAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(palette: Palette) {
            binding.run {
                this.palette = palette
                listener = this@PaletteViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        return PaletteViewHolder(
            PaletteItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) =
        holder.bind(getItem(position))
}