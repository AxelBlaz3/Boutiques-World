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
import com.boutiquesworld.MainActivity
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProductDetailBinding
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Cart
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
    private val isFabric by lazy {
        args.isFabric
    }
    private val productDetailAdapter by lazy {
        ProductDetailImageAdapter()
    }
    private val specificationAdapter by lazy {
        SpecificationAdapter()
    }

    private var fabric: BaseProduct.Fabric? = null

    private var mProductQuantity: Int = 1
    private var mProductPrice: Float = 0F

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
                if (this@ProductDetailFragment.isFabric)
                    productsViewModel.getFabricsLiveData().observe(viewLifecycleOwner) {
                        this@ProductDetailFragment.fabric =
                            (it as ArrayList<BaseProduct.Fabric>).first { fabric -> fabric.productId == this@ProductDetailFragment.productId }

                        // Set a listener to hide or show sticky footer card
                        descNestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                            stickyFooterCard.visibility =
                                if (oldScrollY < scrollY)
                                    View.GONE
                                else
                                    View.VISIBLE
                        }

                        // Check if the product is already in cart
                        // If so, disable the click actions on views.
                        this@ProductDetailFragment.fabric?.let {
                            updateViewsIfNewItemIsPosted(
                                isPosted = cartItems.isNotEmpty() && cartItems.contains(
                                    getCartFromFabric(it).productId
                                ),
                                addToBag = addToBag,
                                add = add,
                                remove = remove,
                                isSoldOut = it.availableMeters == 0
                            )

                            imageUrls.clear()
                            addImages(imageUrls, it)

                            // Assign binding variables
                            this.imageUrls = imageUrls
                            this.fabric = it.copy(
                                productPrice = String.format(
                                    "%.2f",
                                    it.productPrice.toFloat()
                                )
                            )
                            isFabric = true

                            // Set OnClickListeners
                            mProductQuantity = it.availableMeters
                            mProductPrice = it.productPrice.toFloat()
                            addToBag.setOnClickListener { addNewProductToBag() }
                            add.setOnClickListener { _ -> increaseQuantityByOne(it) }
                            remove.setOnClickListener { _ -> reduceQuantityByOne(it) }

                            // Just in case availableMeters is 0, display it as 1, meaning,
                            // Product is unavailable but we display it's price for 1 unit.
                            quantityValue.text =
                                getString(
                                    R.string.quantity_in_meters,
                                    if (it.availableMeters == 0) 1 else it.availableMeters
                                )

                            // Finally submit lists to the adapters.
                            productDetailAdapter.submitList(imageUrls)
                            specificationAdapter.submitList(
                                getSpecifications(
                                    isFabric = true,
                                    product = it
                                )
                            )
                        }
                    }
                else
                    productsViewModel.getProductsLiveData().observe(viewLifecycleOwner) {
                        val product =
                            (it as ArrayList<BaseProduct.Product>).first { product -> product.productId == this@ProductDetailFragment.productId }
                        imageUrls.clear()
                        addImages(imageUrls, product)

                        // Assign binding variables.
                        this.product = product
                        this.imageUrls = imageUrls
                        isFabric = false

                        // Finally submit lists to the adapters.
                        productDetailAdapter.submitList(imageUrls)
                        specificationAdapter.submitList(
                            getSpecifications(
                                isFabric = false,
                                product = product
                            )
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
                    if (it) {
                        cartViewModel.resetIsNewCartItemPosted()


                    }
                }
            }
        }
    }

    /**
     * Add the imagesUrls from product to display them in RecyclerView.
     * @param product: Model class holding either a [com.boutiquesworld.model.BaseProduct.Product] or [com.boutiquesworld.model.BaseProduct.Fabric]
     * @param imageUrls: List where the urls need to be added.
     */
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

    /**
     * Get the specifications for the given product.
     * @param isFabric: Whether the product is [com.boutiquesworld.model.BaseProduct.Fabric], [com.boutiquesworld.model.BaseProduct.Product] otherwise.
     * @param product: [com.boutiquesworld.model.BaseProduct]
     * @return: List<Pair<String, String>>, A list holding the specifications in Key-Value format.
     */
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
            specifications.add(
                Pair(
                    "Quantity",
                    getString(R.string.quantity_in_meters, product.availableMeters)
                )
            )
            specifications.add(Pair("Delivery time", product.deliveryTime ?: "Unknown"))
        } else {
            specifications.add(Pair("Type", (product as BaseProduct.Product).productType))
            specifications.add(Pair("Cloth", product.productCloth ?: "Unknown"))
            specifications.add(Pair("Color", product.productColor ?: "Unknown"))
            specifications.add(Pair("Fabric", product.productFabric ?: "Unknown"))
            specifications.add(Pair("Occasion", product.productOccasion + ""))
            specifications.add(Pair("Stitching time", product.preparationTime ?: "Unknown"))
        }
        return specifications
    }

    /**
     * Grab the required fields from [com.boutiquesworld.model.BaseProduct.Fabric] and use them in [com.boutiquesworld.model.Cart] constructor.
     */
    private fun getCartFromFabric(fabric: BaseProduct.Fabric) =
        Cart(
            productId = fabric.productId.toInt(),
            productName = fabric.productName,
            productDescription = fabric.productDescription,
            productPrice = String.format("%.2f", mProductPrice),
            productType = fabric.productType,
            quantity = mProductQuantity,
            maxQuantity = fabric.availableMeters,
            productImage = fabric.productThumb,
            businessId = fabric.businessId.toInt(),
            userId = profileViewModel.getRetailer().value?.shopId!!,
            userCategory = "B"
        )

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
                cartViewModel.postCartItem(ArrayList<Cart>().apply {
                    add(getCartFromFabric(it))
                })
            }
        }
    }

    /**
     * Reduces the quantity of product by one in the cart.
     */
    private fun reduceQuantityByOne(fabric: BaseProduct.Fabric) {
        binding.run {
            if (fabric.availableMeters > 1 && mProductQuantity > 1) {
                mProductPrice -= mProductPrice / mProductQuantity
                mProductQuantity -= 1
                quantityValue.text =
                    getString(R.string.quantity_in_meters, mProductQuantity)
                price.text = getString(
                    R.string.product_price, String.format(
                        "%.2f", mProductPrice
                    )
                )
            }
        }
    }

    /**
     * Increases the quantity of product by one in the cart.
     */
    private fun increaseQuantityByOne(fabric: BaseProduct.Fabric) {
        binding.run {
            if (mProductQuantity == fabric.availableMeters)
                return
            mProductPrice += mProductPrice / mProductQuantity
            mProductQuantity += 1
            quantityValue.text =
                getString(R.string.quantity_in_meters, mProductQuantity)
            price.text = getString(R.string.product_price, String.format("%.2f", mProductPrice))
        }
    }
}