package com.boutiquesworld.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.databinding.FragmentProductsBinding
import com.boutiquesworld.model.BaseProduct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment(),
    ProductsAdapter.ProductsAdapterListener {
    private lateinit var binding: FragmentProductsBinding
    private val productsAdapter by lazy {
        ProductsAdapter(this)
    }
    private var position: Int = 0

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.get("position") as Int
        }
    }

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
                productsAdapter.submitList(getListForPosition(-1, it))
            }
        else
            productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) { it ->
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

    override fun onProductClick(product: BaseProduct, position: Int) {
        val directions = ProductsFragmentDirections.actionGlobalProductDetailFragment(
            if (product is BaseProduct.Fabric) product.productId else (product as BaseProduct.Product).productId,
            product is BaseProduct.Fabric
        )
        findNavController().navigate(directions)
    }
}