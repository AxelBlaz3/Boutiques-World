package `in`.trendition.ui.requests

import `in`.trendition.R
import `in`.trendition.databinding.FragmentOrderBinding
import `in`.trendition.model.BoutiqueRequest
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BoutiqueRequestFragment. Reuse the same layout that OrderFragment uses.
 */
@AndroidEntryPoint
class BoutiqueRequestFragment : Fragment(), BoutiqueRequestAdapter.BoutiqueRequestAdapterListener {
    private lateinit var binding: FragmentOrderBinding
    private val boutiqueRequestAdapter by lazy {
        BoutiqueRequestAdapter(this)
    }
    private var position = -1

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater)
        (binding.root as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            orderSwipeRefresh.setOnRefreshListener {
                profileViewModel.run {
                    updateBoutiqueRequests(forceRefresh = true)
                    updateBoutiqueResponses(forceRefresh = true)
                }
                orderSwipeRefresh.isRefreshing = false
            }

            orderRecyclerView.adapter = boutiqueRequestAdapter
            profileViewModel.getBoutiqueRequests().observe(viewLifecycleOwner) {
                getListForPosition(it).apply {
                    areProductsEmpty = isNullOrEmpty()
                    if (isNullOrEmpty())
                        when (position) {
                            0 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_pending_products
                                )
                                illustrationTitle = "You're all caught up"
                                illustrationSummary =
                                    "This is where new requests appear"
                            }
                            1 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_products
                                )
                                illustrationTitle = "Waiting for acceptance"
                                illustrationSummary =
                                    "Answered requests appear here"
                            }
                            2 -> {
                                illustration = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_no_cancelled_products
                                )
                                illustrationTitle = "Yay!"
                                illustrationSummary =
                                    "None of your requests are cancelled"
                            }
                        }
                    boutiqueRequestAdapter.submitList(this)
                }
            }
        }
    }

    private fun getListForPosition(requests: ArrayList<BoutiqueRequest>) =
        when (position) {
            0 -> requests.filter { request -> request.requestStatus == 0 }
                .sortedByDescending { request -> request.id }
            1 -> requests.filter { request -> request.requestStatus == 1 }
                .sortedByDescending { request -> request.id }
            2 -> requests.filter { request -> request.requestStatus == 2 }
                .sortedByDescending { request -> request.id }
            else -> throw IllegalArgumentException("Unknown position $position while getting list")
        }

    override fun onBoutiqueRequestClick(request: BoutiqueRequest) {
        if (request.requestStatus == 1) {
            profileViewModel.getBoutiqueResponses().value?.let {
                profileViewModel.getRetailer().value?.let { retailer ->
                    findNavController().navigate(
                        BaseRequestFragmentDirections.actionBaseRequestFragmentToBoutiqueRequestBottomSheetDialog(
                            request,
                            it.first { boutiqueResponse -> boutiqueResponse.requestId == request.id && boutiqueResponse.businessId == retailer.shopId }
                        )
                    )
                }
            }
        } else
            findNavController().navigate(
                BaseRequestFragmentDirections.actionBaseRequestFragmentToBoutiqueRequestBottomSheetDialog(
                    request
                )
            )
    }
}