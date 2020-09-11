package com.boutiquesworld.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.boutiquesworld.R
import com.boutiquesworld.databinding.ActivityMainBinding
import com.boutiquesworld.ui.address.AddressViewModel
import com.boutiquesworld.ui.cart.CartViewModel
import com.boutiquesworld.ui.dashboard.DashboardFragmentDirections
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.boutiquesworld.util.SessionManager
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_layout_basket_badge.view.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    View.OnClickListener, PaymentResultWithDataListener {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var cartViewModel: CartViewModel

    @Inject
    lateinit var addressViewModel: AddressViewModel

    private var menuRes = -1
    private var isCartBadgeInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        verifyStartDestination(savedInstanceState)
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)

        binding.run {
            fab.setOnClickListener(this@MainActivity)
            toolbar.setNavigationOnClickListener { findNavController(R.id.nav_host_fragment).navigateUp() }
            bottomNavView.setupWithNavController(findNavController(R.id.nav_host_fragment))

            profileViewModel.getRetailer().observe(this@MainActivity) {
                when (it.businessCategory) {
                    "B", "D" -> {
                        bottomNavView.inflateMenu(R.menu.boutiques_nav_menu)
                        cartViewModel.updateCart(
                            it.shopId,
                            it.businessCategory,
                            forceRefresh = false
                        )
                    }
                    "F", "Y" -> bottomNavView.inflateMenu(R.menu.fabrics_menu)
                }
            }

            cartViewModel.getAreCartItemsLoaded().observe(this@MainActivity) { areCartItemsLoaded ->
                if (areCartItemsLoaded)
                    addressViewModel.resetIsPaymentCaptured()
                if (!areCartItemsLoaded || isCartBadgeInitialized)
                    return@observe
                initCartBadge()
            }

            addressViewModel.getIsPaymentCaptured()
                .observe(this@MainActivity) { isPaymentCaptured ->
                    if (isPaymentCaptured)
                        profileViewModel.getRetailer().value?.let {
                            cartViewModel.updateCart(
                                it.shopId,
                                it.businessCategory,
                                forceRefresh = true
                            )
                        }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Check if same menu is assigned previously, if so, just return
        if (binding.toolbar.menu.findItem(R.id.cart) != null && binding.toolbar.menu.findItem(
                R.id.notifications
            ) != null && menuRes == R.menu.main_menu
        )
            return true

        binding.toolbar.menu.clear()
        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
            initCartBadge()
        }

        // Set a click listener for basket
        menu?.findItem(R.id.cart)?.actionView?.setOnClickListener {
            findNavController(
                R.id.nav_host_fragment
            ).navigate(DashboardFragmentDirections.actionGlobalCartFragment())
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalCartFragment()
            )
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> profileViewModel.getRetailer().value?.let {
                if (it.businessCategory == "B")
                    findNavController(R.id.nav_host_fragment).navigate(
                        DashboardFragmentDirections.actionGlobalNewProductFragment()
                    )
                else if (it.businessCategory == "F")
                    findNavController(R.id.nav_host_fragment).navigate(
                        DashboardFragmentDirections.actionGlobalFabricNewProductFragment()
                    )
            }
            else -> throw RuntimeException("Unknown widget clicked $v")
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.loginFragment -> {
                supportActionBar?.hide()
                binding.toolbar.navigationIcon = null
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.fabricsFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.fabrics)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.dashboardFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.dashboard)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.profileFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.profile)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.pendingFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.pending)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.fabricProductsFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.products)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.newProductFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.new_product)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.fabricNewProductFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.new_product)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.productDetailFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.empty)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.cartFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.cart)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.addressFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.address)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.bottomSheetPalette -> {
            }
            R.id.orderFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.orders)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            else -> throw RuntimeException("Unknown destination - ${destination.id}")
        }
    }

    /**
     * Handle the startDestination of NavigationGraph. Depends on whether the user's session.
     * When logged in, upon launching will open DashboardFragment. LoginFragment otherwise.
     */
    private fun verifyStartDestination(savedInstanceState: Bundle?) {
        if (savedInstanceState != null)
            return
        val navGraph =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
                .navInflater
                .inflate(R.navigation.nav_graph)

        navGraph.startDestination =
            if (sessionManager.getSession())
                R.id.dashboardFragment
            else
                R.id.loginFragment

        findNavController(R.id.nav_host_fragment).graph = navGraph
    }

    /**
     * Update the title of toolbar with the given string resource
     * @param title: String resource
     */
    private fun updateToolbarTitle(@StringRes title: Int) {
        supportActionBar?.title = getString(title)
    }

    /**
     * Show or hide FAB and BottomAppBar
     * @param hideFab: Hides FAB if true, shows it otherwise.
     * @param hideBottomAppBar: Hides BottomAppBar if true, shows it otherwise.
     */
    private fun showHideFabAndBottomAppBar(hideFab: Boolean, hideBottomAppBar: Boolean) {
        if (hideFab)
            binding.fab.hide()
        else
            binding.fab.show()

        binding.apply {
            if (!hideBottomAppBar)
                bottomAppBar.apply {
                    visibility = View.VISIBLE
                    performShow()
                }
            else {
                bottomAppBar.apply {
                    performShow() // Should trigger animation listener
                    performHide()
                }
                var isCancelled = false
                bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator?) {
                        isCancelled = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (isCancelled)
                            return

                        bottomAppBar.visibility = View.GONE
                        fab.visibility = View.INVISIBLE
                    }
                })
            }
        }
    }

    private fun initCartBadge() {
        cartViewModel.getCart().value?.let {
            if (it.isEmpty())
                binding.toolbar.menu?.findItem(R.id.cart)?.actionView?.apply {
                    setPadding(0, 0, 0, 0)
                    action_item_basket_count?.visibility =
                        View.INVISIBLE
                }
            else
                binding.toolbar.menu?.findItem(R.id.cart)?.actionView?.let { actionView ->
                    actionView.setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.grid_2), 0)
                    actionView.action_item_basket_count?.let { basketCount ->
                        basketCount.visibility = View.VISIBLE
                        basketCount.text = it.size.toString()
                    }
                }
            isCartBadgeInitialized = cartViewModel.getAreCartItemsLoaded().value!!
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {
        addressViewModel.resetRazorPayOrderId()
    }

    override fun onPaymentSuccess(razorPayPaymentId: String?, paymentData: PaymentData?) {
        addressViewModel.resetRazorPayOrderId()
        paymentData?.let {
            val razorPayOrderId = it.orderId
            val paymentId = it.paymentId
            val signature = it.signature
            val orderId = cartViewModel.orderId
            val cartCount = cartViewModel.getCart().value!!.size
            val amount = cartViewModel.cartTotal

            addressViewModel.verifyAndCapturePayment(
                razorPayOrderId,
                paymentId,
                signature,
                orderId,
                cartCount,
                amount.toString()
            )
        }
    }
}
