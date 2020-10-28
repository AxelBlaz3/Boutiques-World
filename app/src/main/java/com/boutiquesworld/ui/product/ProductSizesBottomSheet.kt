package com.boutiquesworld.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.databinding.FragmentProductSizeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductSizesBottomSheet : BottomSheetDialogFragment(),
    ProductSizeAdapter.ProductSizeAdapterListener {
    private lateinit var binding: FragmentProductSizeBottomSheetBinding
    private val productSizeAdapter by lazy {
        ProductSizeAdapter(this)
    }
    private val args: ProductSizesBottomSheetArgs by navArgs()
    private val sizesAvailable by lazy {
        args.sizesAvailable
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductSizeBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            sizesRecyclerView.adapter = productSizeAdapter
            productSizeAdapter.submitList(sizesAvailable.asList().map {
                when (it) {
                    "S" -> "Small (S)"
                    "M" -> "Medium (M)"
                    "L" -> "Large (L)"
                    "XL" -> "Extra Large (XL)"
                    "XXL" -> "Extra Extra Large (XXL)"
                    else -> throw IllegalStateException("Unknown size $it")
                }
            })
        }
    }

    override fun onSizeClicked(size: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "size",
            size.substring(size.indexOf('(') + 1, size.length - 1)
        )
        dismiss()
    }
}