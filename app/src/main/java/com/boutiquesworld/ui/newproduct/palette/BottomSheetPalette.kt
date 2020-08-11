package com.boutiquesworld.ui.newproduct.palette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.databinding.FragmentBottomSheetPaletteDialogBinding
import com.boutiquesworld.model.Palette
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPalette : BottomSheetDialogFragment(), PaletteAdapter.PaletteAdapterListener {
    private lateinit var binding: FragmentBottomSheetPaletteDialogBinding
    private val paletteAdapter by lazy {
        PaletteAdapter(this)
    }

    private val paletteColors by lazy {
        ArrayList<Palette>().apply {
            add(Palette("#000000", "Black"))
            add(Palette("#FFFFFF", "White"))
            add(Palette("#FF0000", "Red"))
            add(Palette("#FFFF00", "Yellow"))
            add(Palette("#808000", "Olive"))
            add(Palette("#000000", "Black"))
            add(Palette("#FFFFFF", "White"))
            add(Palette("#FF0000", "Red"))
            add(Palette("#FFFF00", "Yellow"))
            add(Palette("#808000", "Olive"))
            add(Palette("#000000", "Black"))
            add(Palette("#FFFFFF", "White"))
            add(Palette("#FF0000", "Red"))
            add(Palette("#FFFF00", "Yellow"))
            add(Palette("#808000", "Olive"))
            add(Palette("#000000", "Black"))
            add(Palette("#FFFFFF", "White"))
            add(Palette("#FF0000", "Red"))
            add(Palette("#FFFF00", "Yellow"))
            add(Palette("#808000", "Olive"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetPaletteDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.paletteRecyclerView.adapter = paletteAdapter
        paletteAdapter.submitList(paletteColors)
    }

    override fun onColorClicked(palette: Palette) {
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set("hexColorTint", palette.hexColorTint)
            set("colorName", palette.colorName)
        }
        dismiss()
    }
}