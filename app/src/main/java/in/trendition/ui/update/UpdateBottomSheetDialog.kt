package `in`.trendition.ui.update

import `in`.trendition.BuildConfig
import `in`.trendition.databinding.FragmentUpdateBottomSheetBinding
import `in`.trendition.ui.MainActivityViewModel
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.FileProvider
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.ProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
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
    ): View? {
        isCancelable = false
        binding = FragmentUpdateBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            mainActivityViewModel.getIsUpdateDownloaded()
                .observe(viewLifecycleOwner) { isUpdateDownloaded ->
                    if (!isUpdateDownloaded) {
                        updateAction.run {
                            setOnClickListener {
                                it.isEnabled = false
                                isDownloading = true

                                binding.updateProgress.run {
                                    hide()
                                    isIndeterminate = true
                                    show()
                                }

                                mainActivityViewModel.downloadUpdatePackage(
                                    requireContext().getExternalFilesDir("update").toString()
                                )
                            }
                        }
                    } else {
                        isDownloading = false
                        updateProgress.hide()
                        updateAction.isEnabled = true
                        installUpdatePackage(
                            File(
                                requireContext().getExternalFilesDir("update").toString(),
                                mainActivityViewModel.downloadUrl.substring(
                                    mainActivityViewModel.downloadUrl.lastIndexOf('/') + 1
                                )
                            )
                        )
                    }
                }

            mainActivityViewModel.getDownloadProgress().observe(viewLifecycleOwner) {
                updateProgress.run {
                    hide()
                    isIndeterminate = false
                    show()
                }
                animateProgressIndicator(progressIndicator = updateProgress, toProgress = it)
            }
        }
    }

    private fun installUpdatePackage(updatePackage: File) {
        val installIntent = Intent(Intent.ACTION_VIEW)
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(), "${BuildConfig.APPLICATION_ID}.provider", updatePackage
        )
        installIntent.apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(installIntent)
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
}