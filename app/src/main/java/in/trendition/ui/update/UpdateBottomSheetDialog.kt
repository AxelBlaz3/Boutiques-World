package `in`.trendition.ui.update

import `in`.trendition.BuildConfig
import `in`.trendition.databinding.FragmentUpdateBottomSheetBinding
import `in`.trendition.ui.MainActivityViewModel
import `in`.trendition.util.Constants
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UpdateBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentUpdateBottomSheetBinding

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        binding = FragmentUpdateBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            updateAction.setOnClickListener {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.PLAY_STORE_BASE_URL + BuildConfig.APPLICATION_ID)
                )
                startActivity(playStoreIntent)
            }
        }
    }
}