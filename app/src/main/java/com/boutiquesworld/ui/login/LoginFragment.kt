package com.boutiquesworld.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            loginButton.setOnClickListener {
                loginViewModel.loginRetailer(
                    loginMobileEditText.text.toString(),
                    loginPasswordEditText.text.toString()
                )
            }
        }

        loginViewModel.getIsEmailEmpty().observe(viewLifecycleOwner) {
            if (it)
                binding.loginMobileEditText.error = getString(R.string.empty_mobile)
        }

        loginViewModel.getIsPasswordEmpty().observe(viewLifecycleOwner) {
            if (it)
                binding.loginPasswordEditText.error = getString(R.string.empty_password)
        }

        loginViewModel.getIsLoginSuccessful().observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToProfileFragment())
            else
                Snackbar.make(
                    view,
                    getString(R.string.incorrect_credentials),
                    Snackbar.LENGTH_SHORT
                ).show()
        }
    }
}