package `in`.trendition.ui.login

import `in`.trendition.R
import `in`.trendition.databinding.FragmentLoginBinding
import `in`.trendition.util.SessionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var sessionManager: SessionManager

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
            loginType.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.login_type_array)
                )
            )
            loginType.setText(loginType.adapter.getItem(0).toString(), false)
            loginButton.setOnClickListener {
                loginViewModel.loginRetailer(
                    loginMobileEditText.text.toString(),
                    loginPasswordEditText.text.toString(),
                    loginType.text.toString()[0]
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
                findNavController().navigate(LoginFragmentDirections.actionGlobalDashboardFragment())
            else
                Snackbar.make(
                    view,
                    getString(R.string.incorrect_credentials),
                    Snackbar.LENGTH_SHORT
                ).show()
        }
    }
}