package `in`.trendition.ui.cart

import `in`.trendition.databinding.CartItemBinding
import `in`.trendition.model.Cart
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for Cart.
 */
class CartAdapter(private val listener: CartAdapterListener) :
    ListAdapter<Cart, CartAdapter.CartViewHolder>(CartDiffUtil) {

    interface CartAdapterListener {
        fun onDeleteButtonClick(cart: Cart, position: Int)
        fun onCartItemUpdated(newCartItem: Cart)
        fun onCartListsDiffer(areDifferent: Boolean)
        fun onMinusClicked(cart: Cart, cartQuantity: TextView, cartProductPrice: TextView)
        fun onPlusClicked(cart: Cart, cartQuantity: TextView, cartProductPrice: TextView)
    }

    object CartDiffUtil : DiffUtil.ItemCallback<Cart>() {
        var cartListCount = 0
        var listener: CartAdapterListener? = null
        private var i = 0
        private var areTwoListsSame = true

        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            val areContentsTheSame = oldItem.businessId == newItem.businessId &&
                    oldItem.productCategory == newItem.productCategory &&
                    oldItem.productImage == newItem.productImage &&
                    oldItem.productId == newItem.productId &&
                    oldItem.productName == newItem.productName &&
                    oldItem.productDescription == newItem.productDescription &&
                    oldItem.productPrice == newItem.productPrice &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.availableQuantity == newItem.availableQuantity &&
                    oldItem.userId == newItem.userId &&
                    oldItem.businessCategory == newItem.businessCategory &&
                    oldItem.productType == newItem.productType

            areTwoListsSame = areTwoListsSame && areContentsTheSame
            if (++i == cartListCount) {
                listener?.onCartListsDiffer(!areTwoListsSame)
                i = 0
                areTwoListsSame = true
            }
            return areContentsTheSame
        }
    }

    class CartViewHolder(
        private val binding: CartItemBinding,
        private val listener: CartAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart, position: Int) {
            binding.run {
                this.cart = cart
                this.listener = this@CartViewHolder.listener
                this.position = position
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
        CartViewHolder(
            CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}
