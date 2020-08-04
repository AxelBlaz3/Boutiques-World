package com.boutiquesworld

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.boutiquesworld.databinding.ActivityMainBinding
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
        verifyStartDestination()
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)

        binding.apply {
            home.setOnClickListener(this@MainActivity)
            fabrics.setOnClickListener(this@MainActivity)
            pending.setOnClickListener(this@MainActivity)
            profile.setOnClickListener(this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolbar.menu.clear()
        if (menuRes != -1)
            menuInflater.inflate(menuRes, menu)
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> {
                Log.d(this.javaClass.simpleName, "Home")
            }
            R.id.fabrics -> {
                Log.d(this.javaClass.simpleName, "Fabrics")
            }
            R.id.pending -> {
                Log.d(this.javaClass.simpleName, "Pending")
            }
            else -> {
                Log.d(this.javaClass.simpleName, "Profile")
            }
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
                menuRes = -1
                onCreateOptionsMenu(binding.toolbar.menu)
                showHideFabAndBottomAppBar(hideFab = true, hideBottomAppBar = true)
            }
            R.id.productsFragment -> {
                supportActionBar?.show()
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.dashboardFragment -> {
                supportActionBar?.show()
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            R.id.profileFragment -> {
                supportActionBar?.show()
                menuRes = R.menu.main_menu
                onCreateOptionsMenu(binding.toolbar.menu)
                showHideFabAndBottomAppBar(hideFab = false, hideBottomAppBar = false)
            }
            else -> {
            } // TODO: Handle unknown navigation
        }
    }

    /**
     * Handle the startDestination of NavigationGraph. Depends on whether the user's session.
     * When logged in, upon launching will open DashboardFragment. LoginFragment otherwise.
     */
    private fun verifyStartDestination() {
        val navGraph =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
                .navInflater
                .inflate(R.navigation.nav_graph)

        navGraph.startDestination =
            if (sessionManager.getSession())
                R.id.profileFragment
            else
                R.id.loginFragment

        findNavController(R.id.nav_host_fragment).graph = navGraph
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
}
