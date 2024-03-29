package `in`.trendition.ui.cart

import `in`.trendition.R
import `in`.trendition.databinding.FragmentCartBinding
import `in`.trendition.model.Cart
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment subclass for Cart.
 */
@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy {
        CartAdapter.CartDiffUtil.listener = this
        CartAdapter(this)
    }

    @Inject
    lateinit var cartViewModel: CartViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            cartSwipeRefresh.setOnRefreshListener {
                profileViewModel.getRetailer().value?.let {
                    cartViewModel.run {
                        setIsCartCheckoutClicked(false)
                        updateCart(it.shopId, it.businessCategory, forceRefresh = true)
                    }
                }
                cartSwipeRefresh.isRefreshing = false
            }

            cartRecyclerView.apply {
                adapter = cartAdapter
                // addItemDecoration(CustomDividerItemDecoration())
            }

            cartCheckout.apply {
                setOnClickListener {
                    isEnabled = false
                    cartViewModel.setIsCartCheckoutClicked(true)
                    profileViewModel.getRetailer().value?.let { retailer ->
                        cartViewModel.postCartItem(
                            retailer.shopId,
                            retailer.businessCategory,
                            forceRefresh = true,
                            cart = cartViewModel.finalCartToCheckout
                        )
                    }
                }
            }
        }

        cartViewModel.getCart().observe(viewLifecycleOwner) {
            binding.isCartEmpty = it.isEmpty()
            CartAdapter.CartDiffUtil.cartListCount = it.size
            cartViewModel.finalCartToCheckout = it
            cartAdapter.submitList(it.toMutableList()) {
                binding.run {
                    cartViewModel.cartTotal = getCartTotal(it)
                    total = getString(
                        R.string.product_price,
                        cartViewModel.cartTotal.toString()
                    )
                    cartCheckout.isEnabled = it.isNotEmpty()
                }
            }
        }
    }

    override fun onDeleteButtonClick(cart: Cart, position: Int) {
        cartViewModel.finalCartToCheckout =
            cartAdapter.currentList.toMutableList() as ArrayList<Cart>
        cartViewModel.finalCartToCheckout.run {
            if (size == 1)
                binding.isCartEmpty = true
            removeAt(position)
            CartAdapter.CartDiffUtil.cartListCount -= 1
            cartViewModel.cartTotal -= cart.quantity * cart.productPrice.toInt()
            binding.total =
                getString(R.string.product_price, cartViewModel.cartTotal.toString())
            cartAdapter.submitList(null)
            cartAdapter.submitList(this)
        }
        cartViewModel.deleteCartItem(cart, position)
    }

    override fun onMinusClicked(cart: Cart, cartQuantity: TextView, cartProductPrice: TextView) {
        if (cart.minimumQuantity <= cart.quantity - 1) {
            cartViewModel.updateCartItemQuantity(cart.id, cart.quantity - 1)
            binding.total =
                getString(R.string.product_price, cartViewModel.cartTotal.toString())
        } else
            Snackbar.make(
                binding.root,
                getString(R.string.min_quantity_snackbar, cart.minimumQuantity),
                Snackbar.LENGTH_SHORT
            ).run {
                anchorView = binding.checkoutFooterCard
                show()
            }
        // cartAdapter.submitList(cartViewModel.finalCartToCheckout)
    }

    override fun onPlusClicked(cart: Cart, cartQuantity: TextView, cartProductPrice: TextView) {
        if (cart.availableQuantity >= cart.quantity + 1) {
            cartViewModel.updateCartItemQuantity(cart.id, cart.quantity + 1)
            binding.total =
                getString(R.string.product_price, cartViewModel.cartTotal.toString())
        } else
            Snackbar.make(
                binding.root,
                getString(R.string.max_quantity_snackbar, cart.availableQuantity),
                Snackbar.LENGTH_SHORT
            ).run {
                anchorView = binding.checkoutFooterCard
                show()
            }
        // cartAdapter.submitList(cartViewModel.finalCartToCheckout)
    }

    override fun onCartItemUpdated(newCartItem: Cart) {
        cartViewModel.finalCartToCheckout =
            cartAdapter.currentList.toMutableList() as ArrayList<Cart>
        cartViewModel.finalCartToCheckout.apply {
            val index = indexOfFirst { oldCart -> oldCart.productId == newCartItem.productId }
            if (index > -1) {
                set(index, newCartItem)
                cartViewModel.cartTotal = getCartTotal(this)
                binding.total = getString(
                    R.string.product_price,
                    cartViewModel.cartTotal.toString()
                )
                cartViewModel.updateCartItem(newCartItem)
            }
        }
        cartAdapter.submitList(cartViewModel.finalCartToCheckout)
    }

    override fun onCartListsDiffer(areDifferent: Boolean) {
        Handler(requireContext().mainLooper).post { binding.cartCheckout.isEnabled = true }
        cartViewModel.getIsCartCheckoutClicked().value?.let { isCheckoutClicked ->
            when {
                isCheckoutClicked && !areDifferent -> { // No changes in cart, navigate to billing
                    profileViewModel.getRetailer().value?.let { retailer ->
                        Handler(requireContext().mainLooper).post {
                            // Generate a random 10 digit OrderID
                            cartViewModel.orderId =
                                "ORDER_${retailer.businessCategory}${retailer.shopId}${System.currentTimeMillis()}"
                            findNavController().navigate(
                                CartFragmentDirections.actionCartFragmentToAddressFragment(
                                    retailer.shopId.toString(),
                                    cartViewModel.orderId
                                )
                            )
                        }
                    }
                }
                isCheckoutClicked && areDifferent -> { // Some products stock changed, notify user using a SnackBar
                    Snackbar.make(
                        binding.root,
                        getString(R.string.cart_items_updated),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        anchorView = binding.checkoutFooterCard
                        show()
                    }
                }
                else -> {
                }
            }
        }
    }

    private inline fun <T> Iterable<T>.sumByInt(selector: (T) -> Int): Int {
        var sum = 0
        for (price in this)
            sum += selector(price)
        return sum
    }

    private fun getCartTotal(cart: List<Cart>) =
        cart.sumByInt { cartItem -> cartItem.productPrice.toInt() * cartItem.quantity }

    override fun onDestroyView() {
        super.onDestroyView()
        cartViewModel.setIsCartCheckoutClicked(false)
    }
}