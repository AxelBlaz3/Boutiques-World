package com.boutiquesworld

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.boutiquesworld.databinding.ActivityMainBinding
import com.boutiquesworld.ui.dashboard.DashboardFragmentDirections
import com.boutiquesworld.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    private var menuRes = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        verifyStartDestination(savedInstanceState)
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)

        binding.run {
            home.setOnClickListener(this@MainActivity)
            fabrics.setOnClickListener(this@MainActivity)
            pending.setOnClickListener(this@MainActivity)
            profile.setOnClickListener(this@MainActivity)
            fab.setOnClickListener(this@MainActivity)
            toolbar.setNavigationOnClickListener { findNavController(R.id.nav_host_fragment).navigateUp() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolbar.menu.clear()
        if (menuRes != -1)
            menuInflater.inflate(menuRes, menu)
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
            R.id.home -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalDashboardFragment()
            )
            R.id.fabrics -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalFabricsFragment()
            )
            R.id.pending -> Log.d(this.javaClass.simpleName, "Pending")
            R.id.profile -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalProfileFragment()
            )
            R.id.fab -> findNavController(R.id.nav_host_fragment).navigate(
                DashboardFragmentDirections.actionGlobalNewProductFragment()
            )
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
                binding.fabrics.isChecked = true
                shouldUncheckExcept(destination.id)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.dashboardFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.dashboard)
                binding.home.isChecked = true
                shouldUncheckExcept(destination.id)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.profileFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon = null
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.profile)
                binding.profile.isChecked = true
                shouldUncheckExcept(destination.id)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.newProductFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_back)
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.new_product)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.productDetailFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_back)
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.empty)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.cartFragment -> {
                supportActionBar?.show()
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_back)
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                updateToolbarTitle(R.string.cart)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
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
                bottomAppBar.performHide()
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

    private fun shouldUncheckExcept(destinationId: Int) {
        if (destinationId != R.id.dashboardFragment)
            binding.home.isChecked = false
        if (destinationId != R.id.fabricsFragment)
            binding.fabrics.isChecked = false
        if (destinationId != R.id.profileFragment)
            binding.profile.isChecked = false
    }
}
