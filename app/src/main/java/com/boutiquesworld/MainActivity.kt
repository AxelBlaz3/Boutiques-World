package com.boutiquesworld

import android.os.Bundle
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
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyStartDestination()
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }

    /**
     * Handle the startDestination of NavigationGraph. Depends on whether the user's session.
     * When logged in, upon launching will open ProductsFragment. LoginFragment otherwise.
     */
    private fun verifyStartDestination() {
        val navGraph =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
                .navInflater
                .inflate(R.navigation.nav_graph)

        navGraph.startDestination =
            if (sessionManager.getSession())
                R.id.productsFragment
            else
                R.id.loginFragment

        findNavController(R.id.nav_host_fragment).graph = navGraph
    }
}
