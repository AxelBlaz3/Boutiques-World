package com.boutiquesworld.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSnapHelper
import com.boutiquesworld.databinding.FragmentProductDetailBinding
import com.boutiquesworld.model.BaseProduct
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs()
    private val productId by lazy {
        args.productId
    }
    private val isFabric by lazy {
        args.isFabric
    }
    private val productDetailAdapter by lazy {
        ProductDetailImageAdapter()
    }
    private val specificationAdapter by lazy {
        SpecificationAdapter()
    }

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            stickyFooter.post {
                specificationsRecyclerView.setPadding(
                    specificationsRecyclerView.paddingLeft,
                    specificationsRecyclerView.paddingTop,
                    specificationsRecyclerView.paddingRight,
                    specificationsRecyclerView.paddingBottom + stickyFooter.height
                )
            }
            detailImageRecyclerView.apply {
                LinearSnapHelper().attachToRecyclerView(this)
                adapter = productDetailAdapter
            }
            specificationsRecyclerView.adapter = specificationAdapter
            val imageUrls: ArrayList<String?> = ArrayList()
            if (this@ProductDetailFragment.isFabric)
                productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                    val fabric =
                        (it as ArrayList<BaseProduct.Fabric>).filter { fabric -> fabric.productId == this@ProductDetailFragment.productId }[0]
                    imageUrls.clear()
                    addImages(imageUrls, fabric)

                    this.fabric = fabric
                    isFabric = true
                    this.imageUrls = imageUrls

                    productDetailAdapter.submitList(imageUrls)
                    specificationAdapter.submitList(
                        getSpecifications(
                            isFabric = true,
                            product = fabric
                        )
                    )
                }
            else
                productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                    val product =
                        (it as ArrayList<BaseProduct.Product>).filter { product -> product.productId == this@ProductDetailFragment.productId }[0]
                    imageUrls.clear()
                    addImages(imageUrls, product)

                    this.product = product
                    isFabric = false
                    this.imageUrls = imageUrls

                    productDetailAdapter.submitList(imageUrls)
                    specificationAdapter.submitList(
                        getSpecifications(
                            isFabric = false,
                            product = product
                        )
                    )
                }
        }
    }

    private fun addImages(imageUrls: ArrayList<String?>, product: BaseProduct) {
        if (product is BaseProduct.Fabric)
            imageUrls.apply {
                add(product.productImage1)
                add(product.productImage2)
                if (product.productImage3.isNotEmpty())
                    add(product.productImage3)
                else
                    return@apply
                if (product.productImage4.isNotEmpty())
                    add(product.productImage4)
                else
                    return@apply
                if (product.productImage5.isNotEmpty())
                    add(product.productImage5)
            }
        else
            imageUrls.apply {
                add((product as BaseProduct.Product).productImage1)
                add(product.productImage2)
                if (product.productImage3.isNotEmpty())
                    add(product.productImage3)
                else
                    return@apply
                if (product.productImage4.isNotEmpty())
                    add(product.productImage4)
                else
                    return@apply
                if (product.productImage5.isNotEmpty())
                    add(product.productImage5)
            }
    }

    private fun getSpecifications(
        isFabric: Boolean,
        product: BaseProduct
    ): ArrayList<Pair<String, String>> {
        val specifications = ArrayList<Pair<String, String>>()
        if (isFabric) {
            specifications.add(Pair("Type", (product as BaseProduct.Fabric).productType))
            specifications.add(Pair("Cloth", product.productCloth))
            specifications.add(Pair("Color", product.productColor))
            specifications.add(Pair("Fabric", product.productFabric))
            specifications.add(Pair("Quantity", product.availableMeters + " meters"))
            specifications.add(Pair("Delivery time", product.deliveryTime ?: "Unknown"))
        } else {
            specifications.add(Pair("Type", (product as BaseProduct.Product).productType))
            specifications.add(Pair("Cloth", product.productCloth ?: "Unknown"))
            specifications.add(Pair("Color", product.productColor ?: "Unknown"))
            specifications.add(Pair("Fabric", product.productFabric ?: "Unknown"))
            specifications.add(Pair("Occasion", product.productOccasion + " meters"))
            specifications.add(Pair("Stitching time", product.preparationTime ?: "Unknown"))
        }
        return specifications
    }
}