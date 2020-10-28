package com.boutiquesworld.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProductsBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.StoreCategory
import com.boutiquesworld.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment(),
    ProductsAdapter.ProductsAdapterListener, StoreAdapter.StoreAdapterListener {
    private lateinit var binding: FragmentProductsBinding
    private val productsAdapter by lazy {
        ProductsAdapter(this)
    }
    private val storeAdapter by lazy {
        StoreAdapter(this)
    }
    private var position: Int = 0

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

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
                profileViewModel.getRetailer().value?.let {
                    if (it.businessCategory == "B")
                        productsViewModel.getProducts(forceRefresh = true)
                    else if (it.businessCategory == "D")
                        productsViewModel.getSketches(forceRefresh = true)

                    productsViewModel.getStoreProduct(
                        productCategory = "Clothing",
                        forceRefresh = true
                    )
                    productsViewModel.getStoreProduct(
                        productCategory = "Fabric",
                        forceRefresh = true
                    )
                    productsViewModel.getStoreProduct(
                        productCategory = "Dress Material",
                        forceRefresh = true
                    )
                    productsViewModel.getStoreProduct(
                        productCategory = "Jewellery",
                        forceRefresh = true
                    )
                    productsSwipeRefresh.isRefreshing = false
                }
            }

            // Positions 4, 5 and 6 holds products related to B. 0, 1, 2, 3 related to Store. Designers otherwise.
            if (position == 4 || position == 5 || position == 6) {
                productsRecyclerView.adapter = productsAdapter
                productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                    getListForPosition(position, it).apply {
                        (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                        areProductsEmpty = isNullOrEmpty()
                        if (isNullOrEmpty())
                            when (position) {
                                4 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Start submitting"
                                    illustrationSummary =
                                        "No products yet. Start by clicking the add button"
                                }
                                5 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "Recently submitted products appear here"
                                }
                                6 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_cancelled_products
                                    )
                                    illustrationTitle = "Yay!"
                                    illustrationSummary =
                                        "None of your products are cancelled"
                                }
                            }
                        productsAdapter.submitList(this)
                    }
                }
            } else if (position == 0) {
                productsRecyclerView.adapter = storeAdapter
                productsViewModel.getStoreFabricsLiveData().observe(viewLifecycleOwner) { it ->
                    it.filter { fabric -> (fabric as StoreCategory.Fabric).productStatus == 1 }
                        .run {
                            (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                            areProductsEmpty = isNullOrEmpty()
                            if (isNullOrEmpty()) {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_products
                                )
                                illustrationTitle = "Check back soon"
                                illustrationSummary =
                                    "No products yet. Meanwhile, browse other products."
                            }
                            storeAdapter.submitList(this)
                        }
                }
            } else if (position == 1) {
                productsRecyclerView.adapter = storeAdapter
                productsViewModel.getStoreClothesLiveData().observe(viewLifecycleOwner) { it ->
                    it.filter { cloth -> (cloth as StoreCategory.Cloth).productStatus == 1 }
                        .run {
                            (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                            areProductsEmpty = isNullOrEmpty()
                            if (isNullOrEmpty()) {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_products
                                )
                                illustrationTitle = "Check back soon"
                                illustrationSummary =
                                    "No products yet. Meanwhile, browse other products."
                            }
                            storeAdapter.submitList(this)
                        }
                }
            } else if (position == 2) {
                productsRecyclerView.adapter = storeAdapter
                productsViewModel.getStoreDressMaterialsLiveData()
                    .observe(viewLifecycleOwner) { it ->
                        it.filter { dressMaterial -> (dressMaterial as StoreCategory.DressMaterial).productStatus == 1 }
                            .run {
                                (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                                areProductsEmpty = isNullOrEmpty()
                                if (isNullOrEmpty()) {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Check back soon"
                                    illustrationSummary =
                                        "No products yet. Meanwhile, browse other products."
                                }
                                storeAdapter.submitList(this)
                            }
                    }
            } else if (position == 3) {
                productsRecyclerView.adapter = storeAdapter
                productsViewModel.getStoreJewelleryLiveData().observe(viewLifecycleOwner) { it ->
                    it.filter { jewellery -> (jewellery as StoreCategory.Jewellery).productStatus == 1 }
                        .run {
                            (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                            areProductsEmpty = isNullOrEmpty()
                            if (isNullOrEmpty()) {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_products
                                )
                                illustrationTitle = "Check back soon"
                                illustrationSummary =
                                    "No products yet. Meanwhile, browse other products."
                            }
                            storeAdapter.submitList(this)
                        }
                }
            } else {
                productsRecyclerView.adapter = productsAdapter
                productsViewModel.getSketchesLiveData().observe(viewLifecycleOwner) { it ->
                    getListForPosition(position, it).apply {
                        (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                        areProductsEmpty = isNullOrEmpty()
                        if (isNullOrEmpty())
                            when (position) {
                                10 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Start submitting"
                                    illustrationSummary =
                                        "No products yet. Start by clicking the add button"
                                }
                                11 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "Recently submitted products appear here"
                                }
                                12 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_cancelled_products
                                    )
                                    illustrationTitle = "Yay!"
                                    illustrationSummary =
                                        "None of your products are cancelled"
                                }
                            }
                        productsAdapter.submitList(
                            getListForPosition(
                                position,
                                this as ArrayList<BaseProduct>
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getListForPosition(
        position: Int,
        products: ArrayList<BaseProduct>
    ): MutableList<BaseProduct>? {
        return when (position) {
            4 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 1 } as MutableList<BaseProduct>
            5 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 0 } as MutableList<BaseProduct>
            6 -> products.filter { product -> (product as BaseProduct.Product).productStatus == 2 } as MutableList<BaseProduct>
            // 10, 11, 12 position indicate D (Sketches)
            10 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 1 } as MutableList<BaseProduct>
            11 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 0 } as MutableList<BaseProduct>
            12 -> products.filter { product -> (product as BaseProduct.Sketch).productStatus == 2 } as MutableList<BaseProduct>
            else -> throw IllegalArgumentException("Unknown position $position for getting products list")
        }
    }

    override fun onProductClick(product: BaseProduct, position: Int) {
        profileViewModel.getRetailer().value?.let {
            val directions = if (product is BaseProduct.Product)
                ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    product = product
                )
            else
                ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    sketch = product as BaseProduct.Sketch
                )
            findNavController().navigate(directions)
        }
    }

    override fun onStoreItemClick(storeCategory: StoreCategory, position: Int) {
        val directions =
            when (storeCategory) {
                is StoreCategory.Fabric -> ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    fabric = storeCategory
                )
                is StoreCategory.Cloth -> ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    cloth = storeCategory
                )
                is StoreCategory.DressMaterial -> ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    dressMaterial = storeCategory
                )
                else -> ProductsFragmentDirections.actionGlobalProductDetailFragment(
                    jewellery = storeCategory as StoreCategory.Jewellery
                )
            }
        findNavController().navigate(directions)
    }
}