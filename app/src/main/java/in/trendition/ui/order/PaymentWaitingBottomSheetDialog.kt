package `in`.trendition.ui.order

import `in`.trendition.databinding.FragmentPaymentWaitingBottomSheetBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentWaitingBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPaymentWaitingBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        binding = FragmentPaymentWaitingBottomSheetBinding.inflate(inflater)
        return binding.root
    }
}