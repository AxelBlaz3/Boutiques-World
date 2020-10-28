package com.boutiquesworld.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProductStatusBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.StoreCategory
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
import com.boutiquesworld.ui.product.ProductsViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class ProductStatusFragment : Fragment(), ProductStatusAdapter.ProductStatusAdapterListener {
    private lateinit var binding: FragmentProductStatusBinding
    private val statusAdapter by lazy {
        ProductStatusAdapter(this)
    }
    private var position = -1

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
        binding = FragmentProductStatusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            pendingSwipeRefresh.setOnRefreshListener {
                productsViewModel.getStore(forceRefresh = true)
                productsViewModel.getStoreProduct("Fabric", forceRefresh = true)
                productsViewModel.getStoreProduct("Jewellery", forceRefresh = true)
                productsViewModel.getStoreProduct("Dress Material", forceRefresh = true)
                productsViewModel.getStoreProduct("Clothing", forceRefresh = true)
                pendingSwipeRefresh.isRefreshing = false
            }
            productStatusRecyclerView.adapter = statusAdapter

            productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                getStoreForPosition(position, it).apply {
                    (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                    areProductsEmpty = isNullOrEmpty()
                    if (isNullOrEmpty())
                        when (position) {
                            0 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_products
                                )
                                illustrationTitle = "Start submitting"
                                illustrationSummary =
                                    "No products yet. Start by clicking the add button"
                            }
                            1 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_pending_products
                                )
                                illustrationTitle = "Such empty"
                                illustrationSummary =
                                    "Recently submitted products appear here"
                            }
                            2 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_cancelled_products
                                )
                                illustrationTitle = "Yay!"
                                illustrationSummary =
                                    "None of your products are cancelled"
                            }
                        }
                    statusAdapter.submitList(this as ArrayList<BaseProduct.Store>)
                }
            }
        }
    }

    private fun getStoreForPosition(
        position: Int,
        products: ArrayList<BaseProduct>
    ): List<BaseProduct> {
        return when (position) {
            0 -> products.filter { product -> (product as BaseProduct.Store).productStatus == 1 }
            1 -> products.filter { product -> (product as BaseProduct.Store).productStatus == 0 }
            2 -> products.filter { product -> (product as BaseProduct.Store).productStatus == 2 }
            else -> throw IllegalArgumentException("Unknown position $position while getting store products")
        }
    }

    override fun onSaveButtonClick(store: BaseProduct.Store, productQuantityTextView: TextView) {
        val newQuantity = productQuantityTextView.text.toString()
        if (newQuantity.isNotEmpty())
            productsViewModel.updateQuantityOfStoreProduct(store.productId.toInt(), newQuantity.toInt(), store.productCategory)
    }

    override fun onMinusClicked(productQuantityTextView: TextView) {
        productQuantityTextView.text = "${productQuantityTextView.text.toString().toInt() - 1}"
    }

    override fun onPlusClicked(productQuantityTextView: TextView) {
        productQuantityTextView.text = "${productQuantityTextView.text.toString().toInt() + 1}"
    }
}