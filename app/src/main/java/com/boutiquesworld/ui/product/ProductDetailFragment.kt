package com.boutiquesworld.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSnapHelper
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProductDetailBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Cart
import com.boutiquesworld.model.StoreCategory
import com.boutiquesworld.model.toCart
import com.boutiquesworld.ui.MainActivity
import com.boutiquesworld.ui.cart.CartViewModel
import com.boutiquesworld.ui.newproduct.NewProductViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_layout_basket_badge.view.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs()
    private val sketch by lazy {
        args.sketch
    }
    private val fabric by lazy {
        args.fabric
    }
    private val cloth by lazy {
        args.cloth
    }
    private val dressMaterial by lazy {
        args.dressMaterial
    }
    private val jewellery by lazy {
        args.jewellery
    }
    private val product by lazy {
        args.product
    }
    private val productDetailAdapter by lazy {
        ProductDetailImageAdapter()
    }
    private val specificationAdapter by lazy {
        SpecificationAdapter()
    }
    val sizesAvailable = ArrayList<String>()

    private var mProductQuantity: Int = 1
    private var mProductPrice: Int = 0
    private var mProductSize = ""

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

    @Inject
    lateinit var newProductViewModel: NewProductViewModel

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
            detailImageRecyclerView.apply {
                LinearSnapHelper().attachToRecyclerView(this)
                adapter = productDetailAdapter
                indicator.attachToRecyclerView(this)
            }
            specificationsRecyclerView.adapter = specificationAdapter

            val cartItems = HashMap<String, Cart>()
            cartViewModel.getCart().observe(viewLifecycleOwner) {
                cartItems.clear()
                cartItems.putAll(
                    it.associateBy(
                        { cart -> "${cart.productId}${cart.size}" },
                        { cart -> cart })
                )
                cartViewModel.setAreCartItemsLoaded(true)

                if (it.isEmpty())
                    requireActivity().toolbar.menu?.findItem(R.id.cart)?.actionView?.apply {
                        //setPadding(0, 0, 0, 0)
                        action_item_basket_count?.visibility =
                            View.INVISIBLE
                    }
                else
                    requireActivity().toolbar.menu?.findItem(R.id.cart)?.actionView?.apply {
                        //setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.grid_2), 0)
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

                productDetailsTitle.setOnClickListener {
                    onProductDetailsClicked()
                }

                if (this@ProductDetailFragment.fabric != null || this@ProductDetailFragment.cloth != null || this@ProductDetailFragment.dressMaterial != null || this@ProductDetailFragment.jewellery != null) {
                    this@ProductDetailFragment.fabric?.let {
                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        profileViewModel.getRetailer().value?.let { retailer ->
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && cartItems.contains(it.productId.toString()),
                                addToBag = addToBag,
                                isSoldOut = it.availableQuantity.toInt() == 0
                            )
                        }

                        imageUrls.clear()
                        addImages(imageUrls, it)

                        // Assign binding variables
                        this.fabric = it
                        hexColor =
                            newProductViewModel.paletteColors.first { palette -> palette.colorName == it.productColor }.hexColorTint
                        mProductPrice = it.productPrice.toInt() * it.minimumQuantity.toInt()
                        Log.d(this@ProductDetailFragment.javaClass.simpleName, mProductPrice.toString())
                        // addToBag.text = getString(R.string.bag_at_price, mProductPrice)
                        //price.text = getString(R.string.product_price, mProductPrice.toString())
                        addToBag.setOnClickListener { _ -> addNewProductToBag(it) }

                        mProductQuantity = it.minimumQuantity.toInt()

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getStoreCategorySpecifications(storeCategory = it)
                        )
                    }

                    this@ProductDetailFragment.cloth?.let {
                        // Add available sizes to a list
                        sizesAvailable.clear()
                        sizesAvailable.apply {
                            if (it.sizeS == 1) add("S")
                            if (it.sizeM == 1) add("M")
                            if (it.sizeL == 1) add("L")
                            if (it.sizeXL == 1) add("XL")
                            if (it.sizeXXL == 1) add("XXL")
                        }

                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        profileViewModel.getRetailer().value?.let { retailer ->
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && isClothPosted(
                                    it,
                                    sizesAvailable,
                                    cartItems
                                ),
                                addToBag = addToBag,
                                isSoldOut = it.availableQuantity.toInt() == 0 && it.sizeS == 0 && it.sizeM == 0 && it.sizeL == 0 && it.sizeXL == 0 && it.sizeXXL == 0
                            )
                        }

                        if (it.sizeS == 1 && cartItems.containsKey("${it.productId}S"))
                            sizesAvailable.remove("S")
                        if (it.sizeS == 1 && cartItems.containsKey("${it.productId}M"))
                            sizesAvailable.remove("M")
                        if (it.sizeS == 1 && cartItems.containsKey("${it.productId}L"))
                            sizesAvailable.remove("L")
                        if (it.sizeS == 1 && cartItems.containsKey("${it.productId}XL"))
                            sizesAvailable.remove("XL")
                        if (it.sizeS == 1 && cartItems.containsKey("${it.productId}XXL"))
                            sizesAvailable.remove("XXL")

                        imageUrls.clear()
                        addImages(imageUrls, it)

                        // Set default size to 1st size available and observe for size selection
                        if (sizesAvailable.isNotEmpty()) {
                            mProductSize = sizesAvailable[0]
                            size.text = getString(R.string.size_name, sizesAvailable[0])
                        }
                        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
                            "size"
                        )?.observe(viewLifecycleOwner) {
                            it?.let {
                                mProductSize = it
                                binding.size.text = getString(R.string.size_name, it)
                            }
                        }

                        // Assign binding variables
                        this.cloth = it
                        try {
                            hexColor =
                                newProductViewModel.paletteColors.first { palette -> palette.colorName == it.productColor }.hexColorTint
                        } catch (e: NoSuchElementException) {
                            e.printStackTrace()
                        }

                        // Set OnClickListeners
                        mProductPrice = it.productPrice.toInt()
                        // addToBag.text = getString(R.string.bag_at_price, mProductPrice)
                        addToBag.setOnClickListener { _ -> addNewProductToBag(it) }
                        if (sizesAvailable.isNotEmpty())
                            size.setOnClickListener {
                                findNavController().navigate(
                                    ProductDetailFragmentDirections.actionProductDetailFragmentToProductSizesBottomSheet(
                                        sizesAvailable = sizesAvailable.toTypedArray()
                                    )
                                )
                            }

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getStoreCategorySpecifications(storeCategory = it)
                        )
                    }

                    this@ProductDetailFragment.dressMaterial?.let {

                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        profileViewModel.getRetailer().value?.let { retailer ->
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && cartItems.contains(it.productId.toString()),
                                addToBag = addToBag,
                                isSoldOut = it.availableQuantity.toInt() == 0
                            )
                        }

                        imageUrls.clear()
                        addImages(imageUrls, it)

                        // Assign binding variables
                        this.dressMaterial = it
                        hexColor =
                            newProductViewModel.paletteColors.first { palette -> palette.colorName == it.productColor }.hexColorTint

                        // Set OnClickListeners
                        mProductPrice = it.productPrice.toInt()
                        // addToBag.text = getString(R.string.bag_at_price, mProductPrice)
                        addToBag.setOnClickListener { _ -> addNewProductToBag(it) }

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getStoreCategorySpecifications(storeCategory = it)
                        )
                    }

                    this@ProductDetailFragment.jewellery?.let {

                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        profileViewModel.getRetailer().value?.let { retailer ->
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && cartItems.contains(it.productId.toString()),
                                addToBag = addToBag,
                                isSoldOut = it.availableQuantity.toInt() == 0
                            )
                        }

                        imageUrls.clear()
                        addImages(imageUrls, it)

                        // Assign binding variables
                        this.jewellery = it
                        hexColor =
                            newProductViewModel.jewelleryPalette.first { palette -> palette.colorName == it.productColor }.hexColorTint

                        // Set OnClickListeners
                        mProductPrice = it.productPrice.toInt()
                        // addToBag.text = getString(R.string.bag_at_price, mProductPrice)
                        addToBag.setOnClickListener { _ -> addNewProductToBag(it) }

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getStoreCategorySpecifications(storeCategory = it)
                        )
                    }
                } else if (this@ProductDetailFragment.product != null)
                    this@ProductDetailFragment.product?.let {
                        imageUrls.clear()
                        addImages(imageUrls, it)

                        // Assign binding variables.
                        this.product = it
                        hexColor =
                            newProductViewModel.paletteColors.first { palette -> palette.colorName == it.productColor }.hexColorTint

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getProductSpecifications(product = it)
                        )
                    }
                else
                    this@ProductDetailFragment.sketch.let {
                        imageUrls.clear()
                        addImages(imageUrls, it!!)

                        // Assign binding variables.
                        this.sketch = it

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getProductSpecifications(product = it)
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
                        isPosted = if (this@ProductDetailFragment.cloth == null) it else it && isClothPosted(
                            this@ProductDetailFragment.cloth!!,
                            sizesAvailable,
                            cartItems
                        ),
                        addToBag = addToBag,
                        isSoldOut = if (this@ProductDetailFragment.fabric != null) this@ProductDetailFragment.fabric!!.availableQuantity.toInt() == 0 else if (this@ProductDetailFragment.cloth != null) this@ProductDetailFragment.cloth.let { it!!.availableQuantity.toInt() == 0 && it.sizeS == 0 && it.sizeM == 0 && it.sizeL == 0 && it.sizeXL == 0 && it.sizeXXL == 0 } else if (this@ProductDetailFragment.dressMaterial != null) this@ProductDetailFragment.dressMaterial!!.availableQuantity.toInt() == 0 else this@ProductDetailFragment.jewellery!!.availableQuantity.toInt() == 0
                    )
                    if (it)
                        cartViewModel.resetIsNewCartItemPosted()
                }
            }
        }
    }

    /**
     * Add the imagesUrls from product to display them in RecyclerView.
     * @param product: Model class holding either a [com.boutiquesworld.model.BaseProduct.Product] or [com.boutiquesworld.model.BaseProduct.Sketch]
     * @param imageUrls: List where the urls need to be added.
     */
    private fun addImages(imageUrls: ArrayList<String?>, product: BaseProduct) {
        when (product) {
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

    private fun addImages(imageUrls: ArrayList<String?>, storeCategory: StoreCategory) {
        when (storeCategory) {
            is StoreCategory.Fabric -> {
                imageUrls.apply {
                    add(storeCategory.productImage1)
                    add(storeCategory.productImage2)
                    if (storeCategory.productImage3.isNotEmpty())
                        add(storeCategory.productImage3)
                    else
                        return@apply
                    if (storeCategory.productImage4.isNotEmpty())
                        add(storeCategory.productImage4)
                    else
                        return@apply
                    if (storeCategory.productImage5.isNotEmpty())
                        add(storeCategory.productImage5)
                }
            }
            is StoreCategory.Cloth -> {
                imageUrls.apply {
                    add(storeCategory.productImage1)
                    add(storeCategory.productImage2)
                    if (storeCategory.productImage3.isNotEmpty())
                        add(storeCategory.productImage3)
                    else
                        return@apply
                    if (storeCategory.productImage4.isNotEmpty())
                        add(storeCategory.productImage4)
                    else
                        return@apply
                    if (storeCategory.productImage5.isNotEmpty())
                        add(storeCategory.productImage5)
                }
            }
            is StoreCategory.DressMaterial -> {
                imageUrls.apply {
                    add(storeCategory.productImage1)
                    add(storeCategory.productImage2)
                    if (storeCategory.productImage3.isNotEmpty())
                        add(storeCategory.productImage3)
                    else
                        return@apply
                    if (storeCategory.productImage4.isNotEmpty())
                        add(storeCategory.productImage4)
                    else
                        return@apply
                    if (storeCategory.productImage5.isNotEmpty())
                        add(storeCategory.productImage5)
                }
            }
            is StoreCategory.Jewellery -> {
                imageUrls.apply {
                    add(storeCategory.productImage1)
                    add(storeCategory.productImage2)
                    if (storeCategory.productImage3.isNotEmpty())
                        add(storeCategory.productImage3)
                    else
                        return@apply
                    if (storeCategory.productImage4.isNotEmpty())
                        add(storeCategory.productImage4)
                    else
                        return@apply
                    if (storeCategory.productImage5.isNotEmpty())
                        add(storeCategory.productImage5)
                }
            }
        }
    }

    /**
     * Get the specifications for the given product.
     * @param product: [com.boutiquesworld.model.BaseProduct]
     * @return: List<Pair<String, String>>, A list holding the specifications in Key-Value format.
     */
    private fun getProductSpecifications(
        product: BaseProduct
    ): ArrayList<Pair<String, String>> {
        val specifications = ArrayList<Pair<String, String>>()
        when (product) {
            is BaseProduct.Product -> {
                specifications.add(Pair("Type", product.productType))
                specifications.add(Pair("Cloth", product.productCloth ?: "Unknown"))
                specifications.add(Pair("Color", product.productColor ?: "Unknown"))
                specifications.add(Pair("Fabric", product.productFabric ?: "Unknown"))
                specifications.add(Pair("Occasion", product.productOccasion + ""))
                specifications.add(Pair("Stitching time", product.preparationTime ?: "Unknown"))
            }
            else -> {}
        }
        return specifications
    }

    /**
     * Get the specifications for the given storeCategory.
     * @param storeCategory: [com.boutiquesworld.model.StoreCategory]
     * @return: List<Pair<String, String>>, A list holding the specifications in Key-Value format.
     */
    private fun getStoreCategorySpecifications(
        storeCategory: StoreCategory
    ): ArrayList<Pair<String, String>> {
        val specifications = ArrayList<Pair<String, String>>()
        when (storeCategory) {
            is StoreCategory.Fabric -> {
                specifications.apply {
                    add(Pair("Category", storeCategory.productCategory))
                    add(Pair("Type", storeCategory.productType))
                    add(
                        Pair(
                            "Price",
                            getString(R.string.product_price, storeCategory.productPrice)
                        )
                    )
                    add(Pair("Color", storeCategory.productColor))
                    add(Pair("Cloth", storeCategory.productCloth))
                    add(Pair("Fabric", storeCategory.productFabric))
                    add(Pair("Pattern", storeCategory.productPattern))
                    add(Pair("Width", storeCategory.productWidth))
                    add(Pair("Weight", storeCategory.productWeight))
                    add(Pair("Available quantity", storeCategory.availableQuantity))
                    add(Pair("Delivery in", storeCategory.deliveryTime))
                }
            }
            is StoreCategory.Cloth -> {
                specifications.apply {
                    add(Pair("Category", storeCategory.productCategory))
                    add(Pair("Type", storeCategory.productType))
                    add(Pair("Gender", storeCategory.gender))
                    add(Pair("Occasion", storeCategory.productOccasion))
                    add(
                        Pair(
                            "Price",
                            getString(R.string.product_price, storeCategory.productPrice)
                        )
                    )
                    add(Pair("Color", storeCategory.productColor))
                    add(Pair("Cloth", storeCategory.productCloth))
                    add(Pair("Fabric", storeCategory.productFabric))
                    add(Pair("Available quantity", storeCategory.availableQuantity))
                    add(Pair("Delivery in", storeCategory.deliveryTime))
                }
            }
            is StoreCategory.DressMaterial -> {
                specifications.apply {
                    add(Pair("Category", storeCategory.productCategory))
                    add(Pair("Type", storeCategory.productType))
                    add(Pair("Weight", storeCategory.productWeight))
                    if (storeCategory.setPiece == "Top")
                        add(Pair("Top measurement", storeCategory.topMeasurement))
                    if (storeCategory.setPiece == "Top & Dupatta") {
                        add(Pair("Top measurement", storeCategory.topMeasurement))
                        add(Pair("Dupatta", storeCategory.dupattaMeasurement))
                    } else {
                        add(Pair("Top measurement", storeCategory.topMeasurement))
                        add(Pair("Bottom measurement", storeCategory.bottomMeasurement))
                        add(Pair("Dupatta measurement", storeCategory.dupattaMeasurement))
                    }
                    add(
                        Pair(
                            "Price",
                            getString(R.string.product_price, storeCategory.productPrice)
                        )
                    )
                    add(Pair("Color", storeCategory.productColor))
                    add(Pair("Cloth", storeCategory.productCloth))
                    add(Pair("Fabric", storeCategory.productFabric))
                    add(Pair("Available quantity", storeCategory.availableQuantity))
                    add(Pair("Delivery in", storeCategory.deliveryTime))
                }
            }
            is StoreCategory.Jewellery -> {
                specifications.apply {
                    add(Pair("Category", storeCategory.productCategory))
                    add(Pair("Type", storeCategory.productType))
                    add(Pair("Gender", storeCategory.gender))
                    add(Pair("Material", storeCategory.productMaterial))
                    add(
                        Pair(
                            "Price",
                            getString(R.string.product_price, storeCategory.productPrice)
                        )
                    )
                    add(Pair("Color", storeCategory.productColor))
                    add(Pair("Available quantity", storeCategory.availableQuantity))
                    add(Pair("Delivery in", storeCategory.deliveryTime))
                }
            }
        }
        return specifications
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
    }

    /**
     * Adds new product to cart.
     */
    private fun addNewProductToBag(storeCategory: StoreCategory) {
        disableBackAction.isEnabled = true
        (requireActivity() as MainActivity).toolbar.run {
            menu.findItem(R.id.cart).isEnabled = false
            setNavigationOnClickListener(null)
        }
        binding.run {
            addToBag.isEnabled = false
            profileViewModel.getRetailer().value?.let { retailer ->
                cartViewModel.postCartItem(retailer.shopId,
                    retailer.businessCategory,
                    forceRefresh = true, cart = ArrayList<Cart>().apply {
                        when (storeCategory) {
                            is StoreCategory.Fabric -> storeCategory.toCart(
                                mProductQuantity,
                                mProductPrice.toString(),
                                retailer
                            )
                            is StoreCategory.Cloth -> {
                                storeCategory.toCart(
                                    mProductQuantity,
                                    mProductPrice.toString(),
                                    mProductSize,
                                    retailer
                                )
                            }
                            is StoreCategory.DressMaterial -> storeCategory.toCart(
                                mProductQuantity,
                                mProductPrice.toString(),
                                retailer
                            )
                            else -> (storeCategory as StoreCategory.Jewellery).toCart(
                                mProductQuantity,
                                mProductPrice.toString(),
                                retailer
                            )
                        }.let { cart -> add(cart) }
                    })
            }
        }
    }

    private fun onProductDetailsClicked() {
        binding.run {
            specificationsRecyclerView.apply {
                visibility = if (visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
            }
        }
    }

    private fun isClothPosted(
        cloth: StoreCategory.Cloth,
        sizesAvailable: ArrayList<String>,
        cartItems: HashMap<String, Cart>
    ): Boolean {
        var isPosted = true
        for (size in sizesAvailable) {
            isPosted = isPosted && cartItems.containsKey("${cloth.productId}${size}")
        }
        return isPosted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sizesAvailable.clear()
        mProductQuantity = 1
        mProductPrice = 0
        mProductSize = ""
    }
}