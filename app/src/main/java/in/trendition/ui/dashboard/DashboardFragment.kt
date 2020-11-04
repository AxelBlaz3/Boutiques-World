package `in`.trendition.ui.dashboard

import `in`.trendition.R
import `in`.trendition.databinding.FragmentDashboardBinding
import `in`.trendition.ui.order.OrderViewModel
import `in`.trendition.ui.product.ProductsViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    @Inject
    lateinit var ordersViewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            dashboardSwipeRefreshLayout.setOnRefreshListener {
                profileViewModel.refreshRetailer()
                dashboardSwipeRefreshLayout.isRefreshing = false
            }
            profileViewModel.getRetailer().observe(viewLifecycleOwner) { retailer ->
                dashboardGreetingName.text = retailer.username
                if (retailer.businessCategory == "B" || retailer.businessCategory == "D") {
                    // Set drawables
                    dashboardImage1.setImageResource(R.drawable.ic_round_remove_red_eye)
                    dashboardImage2.setImageResource(R.drawable.ic_round_how_to_reg)
                    dashboardImage3.setImageResource(R.drawable.ic_round_thumb_up)
                    if (retailer.businessCategory == "B")
                        dashboardImage4.setImageResource(R.drawable.ic_round_signal_cellular)
                    else
                        dashboardImage4.setImageResource(R.drawable.ic_cloth)

                    // Set counts
                    dashboardCount1.text = retailer.views
                    dashboardCount2.text = retailer.leads
                    dashboardCount3.text = retailer.likes
                    if (retailer.businessCategory == "B")
                        dashboardCount4.text = retailer.rating
                    else
                        productsViewModel.getSketchesLiveData().observe(viewLifecycleOwner) {
                            dashboardCount4.text = it.size.toString()
                        }

                    // Set captions
                    dashboardCaption1.text = getString(R.string.views)
                    dashboardCaption2.text = getString(R.string.leads)
                    dashboardCaption3.text = getString(R.string.likes)
                    if (retailer.businessCategory == "B")
                        dashboardCaption4.text = getString(R.string.rating)
                    else
                        dashboardCaption4.text = getString(R.string.designs)
                } else {
                    // Store
                    // Set drawables
                    dashboardImage1.setImageResource(R.drawable.ic_round_assignment)
                    dashboardImage2.setImageResource(R.drawable.ic_round_thumb_up)
                    dashboardImage3.setImageResource(R.drawable.ic_round_payments)
                    dashboardImage4.setImageResource(R.drawable.ic_rupee)

                    // Set counts
                    profileViewModel.getRetailer().value?.let { retailer ->
                        ordersViewModel.getOrders().observe(viewLifecycleOwner) { orders ->
                            dashboardCount1.text =
                                orders.filter { order -> order.businessId == retailer.shopId }.size.toString()
                        }
                        dashboardCount2.text = retailer.likes
                        profileViewModel.getPayments().observe(viewLifecycleOwner) { payments ->
                            dashboardCount3.text =
                                payments.filter { payment -> payment.businessId == retailer.shopId }.size.toString()
                            dashboardCount4.text =
                                payments.asSequence()
                                    .filter { payment -> payment.businessId == retailer.shopId }
                                    .sumBy { payment -> payment.productPrice.toInt() }.toString()
                        }
                    }

                    // Set captions
                    dashboardCaption1.text = getString(R.string.orders)
                    dashboardCaption2.text = getString(R.string.likes)
                    dashboardCaption3.text = getString(R.string.payments)
                    dashboardCaption4.text = getString(R.string.total_revenue)
                }
            }
        }
    }
}
