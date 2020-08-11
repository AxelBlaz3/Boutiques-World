package com.boutiquesworld.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentProductsBinding
import com.boutiquesworld.model.BaseProduct
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment(private val position: Int = -1) : Fragment(),
    ProductsAdapter.ProductsAdapterListener {
    private lateinit var binding: FragmentProductsBinding
    private val productsAdapter by lazy {
        ProductsAdapter(this)
    }

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productsRecyclerView.adapter = productsAdapter

        if (position == -1)
            productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                productsAdapter.submitList(getListForPosition(position, it))
            }
        else
            productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                productsAdapter.submitList(getListForPosition(position, it))
            }
    }

    private fun getListForPosition(
        position: Int,
        products: ArrayList<BaseProduct>
    ): MutableList<BaseProduct>? {
        return when (position) {
            -1 -> products
            0 -> products.filter { product ->
                (product as BaseProduct.Fabric).productType.equals(
                    "Fabric",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            1 -> products.filter { product ->
                (product as BaseProduct.Fabric).productType.equals(
                    "Dress Material",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            2 -> products.filter { product ->
                (product as BaseProduct.Fabric).productType.equals(
                    "Saree",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            else -> products.filter { product ->
                (product as BaseProduct.Fabric).productType.equals(
                    "Dupatta",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
        }
    }

    override fun onProductClick(product: BaseProduct.Product, position: Int) {
        // TODO("Not yet implemented")
    }
}