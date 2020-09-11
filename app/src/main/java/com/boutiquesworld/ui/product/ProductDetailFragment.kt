package com.boutiquesworld.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSnapHelper
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProductDetailBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Cart
import com.boutiquesworld.ui.MainActivity
import com.boutiquesworld.ui.cart.CartViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_layout_basket_badge.view.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs()
    private val productId by lazy {
        args.productId
    }
    private val businessCategory by lazy {
        args.businessCategory
    }
    private val productDetailAdapter by lazy {
        ProductDetailImageAdapter()
    }
    private val specificationAdapter by lazy {
        SpecificationAdapter()
    }

    private var store: BaseProduct.Store? = null

    private var mProductQuantity: Int = 1
    private var mProductPrice: Int = 0

    private val disableBackAction: OnBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                // Do nothing
            }
        }

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    @Inject
    lateinit var cartViewModel: CartViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, disableBackAction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
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
                indicator.attachToRecyclerView(this)
            }
            specificationsRecyclerView.adapter = specificationAdapter

            val cartItems = HashMap<Int, Cart>()
            cartViewModel.getCart().observe(viewLifecycleOwner) {
                cartItems.clear()
                cartItems.putAll(it.associateBy({ cart -> cart.productId }, { cart -> cart }))
                cartViewModel.setAreCartItemsLoaded(true)

                if (it.isEmpty())
                    requireActivity().toolbar.menu?.findItem(R.id.cart)?.actionView?.apply {
                        setPadding(0, 0, 0, 0)
                        action_item_basket_count?.visibility =
                            View.INVISIBLE
                    }
                else
                    requireActivity().toolbar.menu?.findItem(R.id.cart)?.actionView?.apply {
                        setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.grid_2), 0)
                        action_item_basket_count?.apply {
                            visibility = View.VISIBLE
                            text = it.size.toString()
                        }
                    }
            }

            cartViewModel.getAreCartItemsLoaded().observe(viewLifecycleOwner) {
                if (!it)
                    return@observe

                val imageUrls: ArrayList<String?> = ArrayList()
                if (this@ProductDetailFragment.businessCategory == "F" || this@ProductDetailFragment.businessCategory == "Y")
                    productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                        this@ProductDetailFragment.store =
                            (it as ArrayList<BaseProduct.Store>).first { fabric -> fabric.productId == this@ProductDetailFragment.productId && fabric.businessCategory == this@ProductDetailFragment.businessCategory }

                        // Set a listener to hide or show sticky footer card
                        descNestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                            stickyFooterCard.visibility =
                                if (oldScrollY < scrollY)
                                    View.GONE
                                else
                                    View.VISIBLE
                        }

                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        this@ProductDetailFragment.store?.let {
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && cartItems.contains(
                                    getCartFromFabric(it)?.productId
                                ),
                                addToBag = addToBag,
                                add = add,
                                remove = remove,
                                isSoldOut = it.availableQuantity == 0
                            )

                            imageUrls.clear()
                            addImages(imageUrls, it)

                            // Assign binding variables
                            this.imageUrls = imageUrls
                            this.fabric = it.copy(
                                productPrice =
                                it.productPrice
                            )

                            // Set OnClickListeners
                            mProductPrice = it.productPrice.toInt()
                            addToBag.setOnClickListener { addNewProductToBag() }
                            add.setOnClickListener { _ -> increaseQuantityByOne(it) }
                            remove.setOnClickListener { _ -> reduceQuantityByOne(it) }

                            // Just in case availableMeters is 0, display it as 1, meaning,
                            // Product is unavailable but we display it's price for 1 unit.
                            quantityValue.text =
                                getString(
                                    R.string.quantity_in_meters,
                                    1
                                )

                            // Finally submit lists to the adapters.
                            productDetailAdapter.submitList(imageUrls)
                            specificationAdapter.submitList(
                                getSpecifications(product = it)
                            )
                        }
                    }
                else if (this@ProductDetailFragment.businessCategory == "B")
                    productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                        val product =
                            (it as ArrayList<BaseProduct.Product>).first { product -> product.productId == this@ProductDetailFragment.productId }
                        imageUrls.clear()
                        addImages(imageUrls, product)

                        // Assign binding variables.
                        this.product = product
                        this.imageUrls = imageUrls

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getSpecifications(product = product)
                        )
                    }
                else
                    productsViewModel.getSketchesLiveData().observe(viewLifecycleOwner) {
                        val sketch =
                            (it as ArrayList<BaseProduct.Sketch>).first { product -> product.productId == this@ProductDetailFragment.productId }
                        imageUrls.clear()
                        addImages(imageUrls, sketch)

                        // Assign binding variables.
                        this.sketch = sketch
                        this.imageUrls = imageUrls

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getSpecifications(product = sketch)
                        )
                    }
            }

            // Observe for changes when adding a new item to cart.
            cartViewModel.getIsNewCartItemPosted().observe(viewLifecycleOwner) {
                disableBackAction.isEnabled = false
                (requireActivity() as MainActivity).toolbar.run {
                    menu.findItem(R.id.cart).isEnabled = true
                    setNavigationOnClickListener { findNavController().navigateUp() }
                }
                it?.let {
                    updateViewsIfNewItemIsPosted(
                        isPosted = it,
                        addToBag = addToBag,
                        add = add,
                        remove = remove,
                        isSoldOut = false
                    )
                    if (it)
                        cartViewModel.resetIsNewCartItemPosted()
                }
            }
        }
    }

    /**
     * Add the imagesUrls from product to display them in RecyclerView.
     * @param product: Model class holding either a [com.boutiquesworld.model.BaseProduct.Product] or [com.boutiquesworld.model.BaseProduct.Store]
     * @param imageUrls: List where the urls need to be added.
     */
    private fun addImages(imageUrls: ArrayList<String?>, product: BaseProduct) {
        when (product) {
            is BaseProduct.Store -> imageUrls.apply {
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
            is BaseProduct.Product -> imageUrls.apply {
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
            else -> imageUrls.apply {
                add((product as BaseProduct.Sketch).productImage1)
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
    }

    /**
     * Get the specifications for the given product.
     * @param product: [com.boutiquesworld.model.BaseProduct]
     * @return: List<Pair<String, String>>, A list holding the specifications in Key-Value format.
     */
    private fun getSpecifications(
        product: BaseProduct
    ): ArrayList<Pair<String, String>> {
        val specifications = ArrayList<Pair<String, String>>()
        when (product) {
            is BaseProduct.Store -> {
                specifications.add(Pair("Type", product.productType))
                specifications.add(Pair("Cloth", product.productCloth))
                specifications.add(Pair("Color", product.productColor))
                specifications.add(Pair("Fabric", product.productFabric))
                specifications.add(
                    Pair(
                        "Quantity",
                        getString(R.string.quantity_in_meters, product.availableQuantity)
                    )
                )
                specifications.add(Pair("Delivery time", product.deliveryTime ?: "Unknown"))
            }
            is BaseProduct.Product -> {
                specifications.add(Pair("Type", product.productType))
                specifications.add(Pair("Cloth", product.productCloth ?: "Unknown"))
                specifications.add(Pair("Color", product.productColor ?: "Unknown"))
                specifications.add(Pair("Fabric", product.productFabric ?: "Unknown"))
                specifications.add(Pair("Occasion", product.productOccasion + ""))
                specifications.add(Pair("Stitching time", product.preparationTime ?: "Unknown"))
            }
            else -> {
                specifications.add(Pair("Type", (product as BaseProduct.Sketch).productType))
                specifications.add(Pair("Cloth", product.productCloth ?: "Unknown"))
                specifications.add(Pair("Color", product.productColor ?: "Unknown"))
                specifications.add(Pair("Fabric", product.productFabric ?: "Unknown"))
                specifications.add(Pair("Occasion", product.productOccasion + ""))
                specifications.add(Pair("Stitching time", product.preparationTime ?: "Unknown"))
            }
        }
        return specifications
    }

    /**
     * Grab the required fields from [com.boutiquesworld.model.BaseProduct.Store] and use them in [com.boutiquesworld.model.Cart] constructor.
     */
    private fun getCartFromFabric(store: BaseProduct.Store): Cart? =
        profileViewModel.getRetailer().value?.let {
            Cart(
                productId = store.productId.toInt(),
                productName = store.productName,
                productDescription = store.productDescription,
                productPrice = mProductPrice.toString(),
                productType = store.productType,
                quantity = mProductQuantity,
                availableQuantity = store.availableQuantity,
                productImage = store.productThumb,
                businessId = store.businessId.toInt(),
                userId = it.shopId,
                businessCategory = it.businessCategory,
                productCategory = store.productType
            )
        }


    /**
     * Enable/Disable views and change the text on MaterialButton if the new cart item is posted or sold out or just normal (Not in cart, Available).
     * @param isPosted: Whether the new cart item is POSTed to server.
     * @param addToBag: Reference to MaterialButton.
     * @param add: Reference to ShapeableImageView.
     * @param remove: Reference to ShapeableImageView.
     * @param: Whether the cart is available.
     */
    private fun updateViewsIfNewItemIsPosted(
        isPosted: Boolean,
        addToBag: MaterialButton,
        add: ShapeableImageView,
        remove: ShapeableImageView,
        isSoldOut: Boolean
    ) {
        addToBag.apply {
            isEnabled = !(isPosted || isSoldOut)
            text = if (isPosted)
                getString(R.string.in_bag)
            else
                if (!isSoldOut)
                    getString(R.string.add_to_bag)
                else
                    getString(R.string.unavailable)
        }
        add.isEnabled = !(isPosted || isSoldOut)
        remove.isEnabled = !(isPosted || isSoldOut)
    }

    /**
     * Adds new product to cart.
     */
    private fun addNewProductToBag() {
        disableBackAction.isEnabled = true
        (requireActivity() as MainActivity).toolbar.run {
            menu.findItem(R.id.cart).isEnabled = false
            setNavigationOnClickListener(null)
        }
        binding.run {
            addToBag.isEnabled = false
            add.isEnabled = false
            remove.isEnabled = false
            fabric?.let {
                profileViewModel.getRetailer().value?.let { retailer ->
                    cartViewModel.postCartItem(retailer.shopId,
                        forceRefresh = true, cart = ArrayList<Cart>().apply {
                            getCartFromFabric(it)?.let { cart -> add(cart) }
                        })
                }
            }
        }
    }

    /**
     * Reduces the quantity of product by one in the cart.
     */
    private fun reduceQuantityByOne(store: BaseProduct.Store) {
        binding.run {
            if (store.availableQuantity > 1 && mProductQuantity > 1) {
                mProductPrice -= store.productPrice.toInt()
                mProductQuantity -= 1
                quantityValue.text =
                    getString(R.string.quantity_in_meters, mProductQuantity)
                price.text = getString(R.string.product_price, mProductPrice.toString())
            }
        }
    }

    /**
     * Increases the quantity of product by one in the cart.
     */
    private fun increaseQuantityByOne(store: BaseProduct.Store) {
        binding.run {
            if (mProductQuantity == store.availableQuantity)
                return
            mProductPrice += store.productPrice.toInt()
            mProductQuantity += 1
            quantityValue.text =
                getString(R.string.quantity_in_meters, mProductQuantity)
            price.text = getString(R.string.product_price, mProductPrice.toString())
        }
    }
}