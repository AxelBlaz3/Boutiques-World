package `in`.trendition.ui.order

import `in`.trendition.databinding.FragmentOrderSuccessBinding
import `in`.trendition.ui.dashboard.DashboardFragmentDirections
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSuccessFragment : Fragment() {
    private lateinit var binding: FragmentOrderSuccessBinding
    private val args: OrderSuccessFragmentArgs by navArgs()
    private val isOrderSuccess by lazy {
        args.isOrderSuccess
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderSuccessBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            isOrderSuccess = this@OrderSuccessFragment.isOrderSuccess
            if (this@OrderSuccessFragment.isOrderSuccess) {
                viewOrdersList.setOnClickListener {
                    findNavController().navigate(DashboardFragmentDirections.actionGlobalMyOrdersFragment())
                }
                backToDashboard.setOnClickListener {
                    findNavController().navigate(DashboardFragmentDirections.actionGlobalDashboardFragment())
                }
            } else {
                // Payment failed, Display `Back to cart` button with followng click listener
                backToDashboard.setOnClickListener {
                    findNavController().navigate(DashboardFragmentDirections.actionGlobalCartFragment())
                }
            }
        }
    }
}