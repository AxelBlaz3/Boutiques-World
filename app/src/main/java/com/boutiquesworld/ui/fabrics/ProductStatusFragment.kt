package com.boutiquesworld.ui.fabrics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentProductStatusBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
import com.boutiquesworld.ui.product.ProductsViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
                profileViewModel.getRetailer().value?.let {
                    productsViewModel.getFabrics(forceRefresh = true)
                    pendingSwipeRefresh.isRefreshing = false
                }
            }
            productStatusRecyclerView.apply {
                addItemDecoration(CustomDividerItemDecoration())
                adapter = statusAdapter
            }
        }

        profileViewModel.getRetailer().observe(viewLifecycleOwner) {
            productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                statusAdapter.submitList(getListForPosition(position, it))
            }
        }
    }

    private fun getListForPosition(
        position: Int,
        products: ArrayList<BaseProduct>
    ): List<BaseProduct.Fabric> {
        return when (position) {
            0 -> products.filter { fabric -> (fabric as BaseProduct.Fabric).productStatus == 1 } as MutableList<BaseProduct.Fabric>
            1 -> products.filter { fabric -> (fabric as BaseProduct.Fabric).productStatus == 0 } as MutableList<BaseProduct.Fabric>
            2 -> products.filter { fabric -> (fabric as BaseProduct.Fabric).productStatus == 2 } as MutableList<BaseProduct.Fabric>
            else -> throw IllegalArgumentException("${this.javaClass.simpleName}: Unknown position - $position for getting list of products")
        }
    }

    override fun onSaveButtonClick(productId: String, productQuantityEditText: TextInputEditText) {
        val newQuantity = productQuantityEditText.text.toString()
        if (newQuantity.isNotEmpty()) {
            Log.d(this.javaClass.simpleName, newQuantity)
            productsViewModel.updateFabric(productId.toInt(), newQuantity.toInt())
        }
    }
}