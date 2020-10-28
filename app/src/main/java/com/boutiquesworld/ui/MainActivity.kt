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
import com.boutiquesworld.ui.login.LoginFragmentDirections
import com.boutiquesworld.ui.order.OrderViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.boutiquesworld.util.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.action_layout_basket_badge.view.*
import java.text.SimpleDateFormat
import java.util.*
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

    @Inject
    lateinit var orderViewModel: OrderViewModel

    private var menuRes = -1
    private var isMenuAssignedPreviously = false
    private var isCartBadgeInitialized = false
    private var isNotificationBadgeInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BoutiquesWorld_DayNight)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        verifyStartDestination(savedInstanceState)
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)

        binding.run {
            fab.setOnClickListener(this@MainActivity)
            toolbar.setNavigationOnClickListener { findNavController(R.id.nav_host_fragment).navigateUp() }
            bottomNavView.setupWithNavController(findNavController(R.id.nav_host_fragment))

            setupObservers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Check if same menu is assigned previously, if so, just return
        profileViewModel.getRetailer().value?.let {
            if (isMenuAssignedPreviously && (it.businessCategory == "B" && menuRes == R.menu.main_menu))
                return true
        }

        binding.toolbar.menu.clear()
        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
            initCartBadge()
            profileViewModel.getRetailer().value?.let {
                if (it.businessCategory == "B")
                    initNotificationBadge()
            }

            // Check if the logged in user is either S, if so, hide menu items.
            profileViewModel.getRetailer().observe(this) {
                when (it.businessCategory) {
                    "S" -> binding.toolbar.menu.apply {
                        findItem(R.id.cart)?.isVisible = false
                        findItem(R.id.myOrdersFragment)?.isVisible = false
                        findItem(R.id.notifications)?.isVisible = false
                    }
                    "D" -> binding.toolbar.menu.apply {
                        findItem(R.id.subscriptionFragment)?.isVisible = false
                        findItem(R.id.notifications)?.isVisible = false
                    }
                    else -> {
                    }
                }
            }

            menu?.apply {
                findItem(R.id.notifications)?.actionView?.action_item_basket?.setImageResource(R.drawable.ic_round_notifications)
                findItem(R.id.cart)?.actionView?.action_item_basket?.setImageResource(R.drawable.ic_round_shopping_cart)
            }

            // Set a click listener for basket/cart
            menu?.findItem(R.id.cart)?.actionView?.setOnClickListener {
                findNavController(
                    R.id.nav_host_fragment
                ).navigate(DashboardFragmentDirections.actionGlobalCartFragment())
            }

            // Set a click listener for boutique requests
            menu?.findItem(R.id.notifications)?.actionView?.setOnClickListener {
                findNavController(
                    R.id.nav_host_fragment
                ).navigate(DashboardFragmentDirections.actionGlobalBaseRequestFragment())
            }

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalCartFragment()
            )
            R.id.myOrdersFragment -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalMyOrdersFragment()
            )
            R.id.subscriptionFragment ->
                profileViewModel.getRetailer().value?.let {
                    if (it.businessCategory == "B")
                        findNavController(R.id.nav_host_fragment).navigate(
                            DashboardFragmentDirections.actionGlobalSubscriptionFragment()
                        )
                    else
                        findNavController(R.id.nav_host_fragment).navigate(
                            DashboardFragmentDirections.actionGlobalPaymentsFragment()
                        )
                }
            R.id.notifications
            -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalBaseRequestFragment()
            )
            R.id.logout -> profileViewModel.logout()
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> profileViewModel.getRetailer().value?.let {
                if (it.businessCategory == "B" || it.businessCategory == "D")
                    findNavController(R.id.nav_host_fragment).navigate(
                        DashboardFragmentDirections.actionGlobalNewProductFragment()
                    )
                else if (it.businessCategory == "F" || it.businessCategory == "Y" || it.businessCategory == "S")
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
            R.id.storeFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.store)
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
            R.id.storeProductsFragment -> {
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
            R.id.storeNewProductFragment -> {
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
                menuRes = R.menu.address_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.address)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.baseOrderFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.orders)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.myOrdersFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_arrow_back
                )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.my_orders)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.orderSummaryFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_round_arrow_back
                    )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.order_summary)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.orderSuccessFragment -> {
                supportActionBar?.hide()
                binding.toolbar.navigationIcon = null
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.orders)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.orderTrackingFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_arrow_back
                )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.order_tracking)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.paymentsFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_arrow_back
                )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.payments)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.baseRequestFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_arrow_back
                )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.requests)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.subscriptionFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_arrow_back
                )
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.subscriptions)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.boutiqueRequestBottomSheetDialog, R.id.bottomSheetPalette, R.id.orderConfirmBottomSheetDialog, R.id.bottomSheetRenewSubscriptionFragment, R.id.productSizesBottomSheet -> {
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
                    // setPadding(0, 0, 0, 0)
                    action_item_basket_count?.visibility =
                        View.INVISIBLE
                }
            else
                binding.toolbar.menu?.findItem(R.id.cart)?.actionView?.let { actionView ->
                    // actionView.setPadding(0, 0, resources.getDimensionPixelSize(R.dimen.grid_2), 0)
                    actionView.action_item_basket_count?.let { basketCount ->
                        basketCount.visibility = View.VISIBLE
                        basketCount.text = it.size.toString()
                    }
                }
            isCartBadgeInitialized = cartViewModel.getAreCartItemsLoaded().value!!
        }
    }

    private fun initNotificationBadge() {
        profileViewModel.getBoutiqueRequests().value?.let {
            binding.toolbar.menu.run {
                findItem(R.id.notifications)?.let { menuItem ->
                    if (menuItem.isVisible) {
                        menuItem.actionView?.action_item_basket_count?.apply {
                            if (it.isNotEmpty()) {
                                text =
                                    it.filter { boutiqueRequest -> boutiqueRequest.requestStatus == 0 }.size.toString()
                                visibility = View.VISIBLE
                            } else
                                visibility = View.INVISIBLE
                        }
                    }
                }
            }
            isNotificationBadgeInitialized = profileViewModel.getAreRequestsLoaded().value!!
        }
    }

    private fun setupObservers() {
        binding.run {
            profileViewModel.getIsLogoutSuccessful().observe(this@MainActivity) {
                it?.let {
                    if (it)
                        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionGlobalLoginFragment())
                }
            }

            profileViewModel.getRetailer().observe(this@MainActivity) {
                // Subscribe to firebase topic
                FirebaseMessaging.getInstance().apply {
                    isAutoInitEnabled = true
                    if (it.businessCategory == "S" || it.businessCategory == "Y" || it.businessCategory == "F")
                        subscribeToTopic("orders")
                    else
                        subscribeToTopic("requests")
                }

                // Setup BottomNavigationMenu
                when (it.businessCategory) {
                    "B", "D" -> {
                        bottomNavView.menu.clear()
                        bottomNavView.inflateMenu(R.menu.boutiques_nav_menu)
                        cartViewModel.updateCart(
                            it.shopId,
                            it.businessCategory,
                            forceRefresh = false
                        )
                    }
                    "S" -> {
                        bottomNavView.menu.clear()
                        bottomNavView.inflateMenu(R.menu.store_menu)
                    }
                }
            }

            cartViewModel.getAreCartItemsLoaded().observe(this@MainActivity) { areCartItemsLoaded ->
                if (areCartItemsLoaded)
                    addressViewModel.resetIsPaymentCaptured()
                if (!areCartItemsLoaded || isCartBadgeInitialized)
                    return@observe
                initCartBadge()
            }

            profileViewModel.getAreRequestsLoaded()
                .observe(this@MainActivity) { areRequestsLoaded ->
                    if (!areRequestsLoaded)
                        return@observe
                    initNotificationBadge()
                }

            addressViewModel.getIsPaymentCaptured()
                .observe(this@MainActivity) { isPaymentCaptured ->
                    isPaymentCaptured?.let {
                        if (isPaymentCaptured) {
                            profileViewModel.getRetailer().value?.let {
                                cartViewModel.updateCart(
                                    it.shopId,
                                    it.businessCategory,
                                    forceRefresh = true
                                )
                                addressViewModel.updateOrderAddressList(it.shopId, forceRefresh = true)
                            }
                            orderViewModel.updateOrders(forceRefresh = true)
                        }
                        val directions =
                            DashboardFragmentDirections.actionGlobalOrderSuccessFragment(
                                isOrderSuccess = isPaymentCaptured
                            )
                        findNavController(R.id.nav_host_fragment).navigate(directions)
                    }
                }

            orderViewModel.getOrders().observe(this@MainActivity) { orders ->
                profileViewModel.getRetailer().value?.let { retailer ->
                    if (retailer.businessCategory == "S") {
                        orders.filter { order -> order.orderStatus == 0 && order.orderStatus != 7 }
                            .run {
                                if (isNotEmpty())
                                    bottomNavView.getOrCreateBadge(R.id.baseOrderFragment).apply {
                                        number = size
                                        isVisible = true
                                    }
                                else
                                    bottomNavView.removeBadge(R.id.baseOrderFragment)
                            }
                    }
                }
            }
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {
        if (profileViewModel.isSubscriptionPayment) {
            profileViewModel.isSubscriptionPayment = false
            profileViewModel.setIsSubscriptionSuccessful(false)
            Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT)
                .apply {
                    anchorView = binding.fab
                    show()
                }
        } else {
            addressViewModel.apply {
                setIsPaymentCaptured(false)
                resetRazorPayOrderId()
            }
        }
    }

    override fun onPaymentSuccess(razorPayPaymentId: String?, paymentData: PaymentData?) {
        if (profileViewModel.isSubscriptionPayment) {
            profileViewModel.isSubscriptionPayment = false

            profileViewModel.getRetailer().value?.let { retailer ->
                paymentData?.let {
                    profileViewModel.subscriptionPlan?.let { subscriptionPlan ->
                        profileViewModel.verifySubscription(
                            subscriptionPlan.id,
                            retailer.shopId,
                            retailer.businessName,
                            retailer.uuid,
                            subscriptionPlan.planAmount.toString(),
                            getFormattedDate(needToday = true),
                            getFormattedDate(
                                needToday = false,
                                period = subscriptionPlan.planPeriod.toInt()
                            ),
                            it.orderId,
                            it.paymentId,
                            it.signature,
                            profileViewModel.subscriptionId
                        )
                    }
                }
                profileViewModel.subscriptionId = ""
            }
        } else {
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

    fun getFormattedDate(needToday: Boolean, period: Int = 0): String {
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, period)
            val date = calendar.time
            val formatter = SimpleDateFormat("MMM d, y")
            return formatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
