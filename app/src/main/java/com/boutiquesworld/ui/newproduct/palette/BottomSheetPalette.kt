package com.boutiquesworld.ui.newproduct.palette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.databinding.FragmentBottomSheetPaletteDialogBinding
import com.boutiquesworld.model.Palette
import com.boutiquesworld.ui.newproduct.NewProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetPalette : BottomSheetDialogFragment(), PaletteAdapter.PaletteAdapterListener {
    private lateinit var binding: FragmentBottomSheetPaletteDialogBinding
    private val args: BottomSheetPaletteArgs by navArgs()
    private val showColorsForJewellery by lazy {
        args.showColorsForJewellery
    }
    private val paletteAdapter by lazy {
        PaletteAdapter(this)
    }

    @Inject
    lateinit var newProductViewModel: NewProductViewModel

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
        if (showColorsForJewellery)
            paletteAdapter.submitList(newProductViewModel.jewelleryPalette)
        else
            paletteAdapter.submitList(newProductViewModel.paletteColors)
    }

    override fun onColorClicked(palette: Palette) {
        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
            set("hexColorTint", palette.hexColorTint)
            set("colorName", palette.colorName)
        }
        dismiss()
    }
}