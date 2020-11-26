package `in`.trendition.ui.newproduct

import `in`.trendition.databinding.FragmentProductPostingBottomSheetBinding
import `in`.trendition.ui.MainActivityViewModel
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.ProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductPostingBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentProductPostingBottomSheetBinding

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        isCancelable = false
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductPostingBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("isSubmissionDone")
                ?.observe(viewLifecycleOwner) {
                    // Reset upload progress to 0 and close the dialog.
                    mainActivityViewModel.setUploadProgress(progress = 0)
                    dismiss()
                    findNavController().navigateUp()
                }

            mainActivityViewModel.getUploadProgress().observe(viewLifecycleOwner) {
                uploadPercentage = it
                if (it >= 99 || it == 0)
                    setIsProgressIndicatorInDeterminate(
                        progressIndicator = binding.progressBar,
                        isIndeterminate = true
                    )
                else {
                    setIsProgressIndicatorInDeterminate(
                        binding.progressBar,
                        isIndeterminate = false
                    )
                    animateProgressIndicator(progressIndicator = progressBar, toProgress = it)
                }
            }
        }
    }

    private fun animateProgressIndicator(progressIndicator: ProgressIndicator, toProgress: Int) {
        binding.run {
            ObjectAnimator.ofInt(
                progressIndicator,
                "progress",
                progressIndicator.progress,
                toProgress
            ).apply {
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    private fun setIsProgressIndicatorInDeterminate(
        progressIndicator: ProgressIndicator,
        isIndeterminate: Boolean
    ) {
        progressIndicator.run {
            visibility = View.GONE
            this.isIndeterminate = isIndeterminate
            visibility = View.VISIBLE
        }
    }
}