package com.boutiquesworld.ui.cart

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentCartBinding
import com.boutiquesworld.model.Cart
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
import com.boutiquesworld.ui.profile.ProfileViewModel
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
                addItemDecoration(CustomDividerItemDecoration())
            }

            cartCheckout.apply {
                if (cartAdapter.currentList.isEmpty())
                    isEnabled = false
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
            CartAdapter.CartDiffUtil.cartListCount = it.size
            cartViewModel.finalCartToCheckout = it
            cartAdapter.submitList(it) {
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

    override fun onDeleteButtonClick(cart: Cart) {
        cartViewModel.finalCartToCheckout =
            cartAdapter.currentList.toMutableList() as ArrayList<Cart>
        cartViewModel.finalCartToCheckout.apply {
            val index = indexOfFirst { oldCart -> oldCart.productId == cart.productId }
            if (index < 0)
                return@apply
            if (size == 1)
                binding.cartCheckout.isEnabled = false
            removeAt(index)
            CartAdapter.CartDiffUtil.cartListCount -= 1
            cartViewModel.cartTotal -= cart.productPrice.toInt()
            binding.total =
                getString(R.string.product_price, cartViewModel.cartTotal.toString())
        }
        cartAdapter.submitList(cartViewModel.finalCartToCheckout)
        cartViewModel.deleteCartItem(cart)
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
                            cartViewModel.orderId = (1000000000..9999999999).random().toString()
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
        cart.sumByInt { cartItem -> cartItem.productPrice.toInt() }

    override fun onDestroyView() {
        super.onDestroyView()
        cartViewModel.setIsCartCheckoutClicked(false)
    }
}