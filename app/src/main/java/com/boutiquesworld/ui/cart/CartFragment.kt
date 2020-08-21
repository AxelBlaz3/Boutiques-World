package com.boutiquesworld.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentCartBinding
import com.boutiquesworld.model.Cart
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
import com.boutiquesworld.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment subclass for Cart.
 */
@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {
    private lateinit var binding: FragmentCartBinding
    private var mTotal = 0F
    private val cartAdapter by lazy {
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
                    cartViewModel.updateCart(it.shopId, "B", forceRefresh = true)
                }
                cartSwipeRefresh.isRefreshing = false
            }
            cartRecyclerView.apply {
                adapter = cartAdapter
                addItemDecoration(CustomDividerItemDecoration())
            }
        }

        cartViewModel.getCart().observe(viewLifecycleOwner) {

            cartAdapter.submitList(it) {
                binding.run {
                    mTotal = getCartTotal(it)
                    total = getString(
                        R.string.product_price,
                        String.format("%.2f", mTotal)
                    )
                }
            }
        }
    }

    override fun onDeleteButtonClick(cart: Cart) {
        val tempCart = cartAdapter.currentList.toMutableList().apply {
            val index = indexOfFirst { oldCart -> oldCart.productId == cart.productId }
            if (index < 0)
                return@apply
            removeAt(index)
            mTotal -= cart.productPrice.toFloat()
            binding.total = getString(R.string.product_price, String.format("%.2f", mTotal))
        }
        cartAdapter.submitList(tempCart)
        cartViewModel.deleteCartItem(cart)
    }

    override fun onCartItemUpdated(newCartItem: Cart) {
        cartAdapter.currentList.toMutableList().apply {
            val index = indexOfFirst { oldCart -> oldCart.productId == newCartItem.productId }
            if (index > -1) {
                set(index, newCartItem)
                mTotal = getCartTotal(this)
                binding.total = getString(R.string.product_price, String.format("%.2f", mTotal))
                cartViewModel.updateCartItem(newCartItem)
            }
        }
    }

    private inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
        var sum = 0F
        for (price in this)
            sum += selector(price)
        return sum
    }

    private fun getCartTotal(cart: List<Cart>) =
        cart.sumByFloat { cartItem -> cartItem.productPrice.toFloat() }
}