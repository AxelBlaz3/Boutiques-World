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

        binding.run {
            productsSwipeRefresh.setOnRefreshListener {
                productsViewModel.getProducts(forceRefresh = true)
                productsViewModel.getFabrics(forceRefresh = true)
                productsSwipeRefresh.isRefreshing = false
            }
            productsRecyclerView.adapter = productsAdapter
        }

        // Positions -1, 4, 5 and 6 holds products related to B. 7, 8, 9 related to Y. 0, 1, 2, 3 related to Store. Designers otherwise.
        if (position == -1 || position == 4 || position == 5 || position == 6)
            productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                productsAdapter.submitList(getListForPosition(position, it))
            }
        else if (position == 0 || position == 1 || position == 2 || position == 3)
            productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) { it ->
                productsAdapter.submitList(getListForPosition(position, it))
            }
        else
            productsViewModel.getSketchesLiveData().observe(viewLifecycleOwner) { it ->
                productsAdapter.submitList(getListForPosition(position, it))
            }
    }

    private fun getListForPosition(
        position: Int,
        products: ArrayList<BaseProduct>
    ): MutableList<BaseProduct>? {
        return when (position) {
            -1 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 1 } as MutableList<BaseProduct>
            0 -> products.filter { product ->
                (product as BaseProduct.Store).productType.equals(
                    "Fabric",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            1 -> products.filter { product ->
                (product as BaseProduct.Store).productType.equals(
                    "Dress Material",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            2 -> products.filter { product ->
                (product as BaseProduct.Store).productType.equals(
                    "Saree",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            3 -> products.filter { product ->
                (product as BaseProduct.Store).productType.equals(
                    "Dupatta",
                    ignoreCase = true
                )
            } as MutableList<BaseProduct>
            4 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 1 } as MutableList<BaseProduct>
            5 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 0 } as MutableList<BaseProduct>
            6 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 2 } as MutableList<BaseProduct>
            // 7, 8, 9 positions indicate Y
            7 -> products.filter { product -> (product as BaseProduct.Store).businessCategory == "Y" && product.productStatus == 1 } as MutableList<BaseProduct>
            8 -> products.filter { product -> (product as BaseProduct.Store).businessCategory == "Y" && product.productStatus == 0 } as MutableList<BaseProduct>
            9 -> products.filter { product -> (product as BaseProduct.Store).businessCategory == "Y" && product.productStatus == 2 } as MutableList<BaseProduct>
            // 10, 11, 12 position indicate D
            10 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 1 } as MutableList<BaseProduct>
            11 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 0 } as MutableList<BaseProduct>
            12 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 2 } as MutableList<BaseProduct>
            else -> throw IllegalArgumentException("Unknown position $position for getting products list")
        }
    }

    override fun onProductClick(product: BaseProduct, position: Int) {
        val directions = ProductsFragmentDirections.actionGlobalProductDetailFragment(
            if (product is BaseProduct.Store) product.productId else if (product is BaseProduct.Product) product.productId else (product as BaseProduct.Sketch).productId,
            if (product is BaseProduct.Store) product.businessCategory else if (product is BaseProduct.Product) "B" else "D"
        )
        findNavController().navigate(directions)
    }
}