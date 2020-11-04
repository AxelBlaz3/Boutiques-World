package `in`.trendition.ui.order

import `in`.trendition.R
import `in`.trendition.databinding.FragmentOrderConfirmBottomSheetBinding
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OrderConfirmBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOrderConfirmBottomSheetBinding
    private val args: OrderConfirmBottomSheetDialogArgs by navArgs()
    private val order by lazy {
        args.order
    }

    @Inject
    lateinit var orderViewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        binding = FragmentOrderConfirmBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.peekHeight = sheet.height
                sheet.parent.parent.requestLayout()
            }
        }

        binding.run {
            order = this@OrderConfirmBottomSheetDialog.order
            orderAction.setOnClickListener {
                it.isEnabled = false
                orderSheetClose.isEnabled = false
                this@OrderConfirmBottomSheetDialog.order.orderId?.let { orderId ->
                    orderViewModel.confirmOrDispatchOrder(
                        this@OrderConfirmBottomSheetDialog.order,
                        trackingId.text.toString(),
                        courierName.text.toString(),
                        estimatedDate.text.toString()
                    )
                }
            }

            orderSheetClose.setOnClickListener { dismiss() }
            estimatedDate.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.estimated_date)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder().setValidator(DateValidator())
                            .build()
                    )
                    .setTheme(R.style.Theme_Trendition_Calendar)
                    .build()
                picker.addOnPositiveButtonClickListener { estimatedDate.text = picker.headerText }
                picker.show(parentFragmentManager, "datePicker")
            }

            orderViewModel.getIsTrackingIdEmpty().observe(viewLifecycleOwner) {
                it?.let { isTrackingIdEmpty ->
                    orderSheetClose.isEnabled = true
                    if (isTrackingIdEmpty) {
                        orderAction.isEnabled = true
                        requestFocusForEditText(trackingId)
                    }
                    orderViewModel.resetIsTrackingIdEmpty()
                }
            }

            orderViewModel.getIsServiceProviderEmpty().observe(viewLifecycleOwner) {
                it?.let { isServiceProviderEmpty ->
                    orderSheetClose.isEnabled = true
                    if (isServiceProviderEmpty) {
                        orderAction.isEnabled = true
                        requestFocusForEditText(courierName)
                    }
                    orderViewModel.resetIsServiceProviderEmpty()
                }
            }

            orderViewModel.getIsDeliveryDateEmpty().observe(viewLifecycleOwner) {
                it?.let { isDeliveryDateEmpty ->
                    orderSheetClose.isEnabled = true
                    if (isDeliveryDateEmpty) {
                        orderAction.isEnabled = true
                        Snackbar.make(orderAction, "Forgot to choose date?", Snackbar.LENGTH_SHORT)
                            .apply {
                                anchorView = orderAction
                                show()
                            }
                    }
                    orderViewModel.resetIsDeliveryDateEmpty()
                }
            }

            orderViewModel.getIsOrderConfirmedOrDispatched().observe(viewLifecycleOwner) {
                it?.let { isOrderConfirmedOrDispatched ->
                    if (isOrderConfirmedOrDispatched)
                        dismiss()
                    else {
                        orderSheetClose.isEnabled = true
                        orderAction.isEnabled = true
                    }
                    orderViewModel.resetIsOrderConfirmedOrDispatched()
                }
            }
        }
    }

    /**
     * Focus on the field that requires input from the user.
     * @param textInputEditText: EditText for being focused.
     */
    private fun requestFocusForEditText(textInputEditText: TextInputEditText) {
        textInputEditText.requestFocus()
    }

    class DateValidator: CalendarConstraints.DateValidator {
        private val dateFormat = SimpleDateFormat("yyyyMMdd")
        override fun describeContents(): Int = 0

        override fun writeToParcel(dest: Parcel?, flags: Int) {
        }

        override fun isValid(date: Long): Boolean =
            Date(date).after(Date(System.currentTimeMillis())) || dateFormat.format(date) == dateFormat.format(System.currentTimeMillis())
    }
}