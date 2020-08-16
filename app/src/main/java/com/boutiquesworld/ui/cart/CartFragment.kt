package com.boutiquesworld.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentCartBinding
import com.boutiquesworld.model.Cart
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment subclass for Cart.
 */
@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy {
        CartAdapter(this)
    }

    @Inject
    lateinit var cartViewModel: CartViewModel

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

        binding.cartRecyclerView.adapter = cartAdapter

        cartViewModel.getCart().observe(viewLifecycleOwner) {
            cartAdapter.submitList(it)
        }
    }

    override fun onAddButtonClick(quantityView: TextView, priceView: TextView, cart: Cart) {
        cartViewModel.updateCartItem(cart, shouldAdd = true)
    }

    override fun onRemoveButtonClick(quantityView: TextView, priceView: TextView, cart: Cart) {
        if (cart.quantity == 1) {
            onDeleteButtonClick(cart)
            return
        }
        cartViewModel.updateCartItem(cart, shouldAdd = false)
    }

    override fun onDeleteButtonClick(cart: Cart) {
        val tempCart = cartAdapter.currentList.toMutableList().apply { removeAt(indexOf(cart)) }
        cartAdapter.submitList(tempCart)
        cartViewModel.deleteCartItem(cart)
    }
}