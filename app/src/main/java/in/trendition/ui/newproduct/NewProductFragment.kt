package `in`.trendition.ui.newproduct

import `in`.trendition.R
import `in`.trendition.databinding.FragmentNewProductBinding
import `in`.trendition.ui.MainActivityViewModel
import `in`.trendition.ui.product.ProductsViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import `in`.trendition.util.FileUtils
import `in`.trendition.util.ProgressUpload
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class NewProductFragment : Fragment(), ProgressUpload.UploadListener {
    private lateinit var binding: FragmentNewProductBinding
    private val REQUEST_IMAGE = 1

    // Map to keep track of index and imageView.
    private val imageMap by lazy {
        HashMap<Int, ShapeableImageView>().apply {
            put(0, binding.productImage1)
            put(1, binding.productImage2)
            put(2, binding.productImage3)
            put(3, binding.productImage4)
            put(4, binding.productImage5)
        }
    }

    // Keep track of currently clicked image selection index.
    var currentImageSelectionIndex = 0

    private var imageFiles = ArrayList<File?>().apply {
        // Assign initial values to 5 images
        add(null)
        add(null)
        add(null)
        add(null)
        add(null)
    }

    @Inject
    lateinit var newProductViewModel: NewProductViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var colorName = ""

        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<String>("hexColorTint").observe(viewLifecycleOwner) {
                it?.let {
                    binding.colorPalette.setColorFilter(Color.parseColor(it))
                }
            }

            getLiveData<String>("colorName").observe(viewLifecycleOwner) {
                it?.let {
                    colorName = it
                    binding.colorNameText.text = colorName
                }
            }
        }

        binding.run {
            // Setup same height and width of ImageView
            productImage1.post {
                productImage1.layoutParams.height = productImage1.width

                profileViewModel.getRetailer().value?.let {
                    isSketch = it.businessCategory == "D"
                }
            }

            // Setup adapters for category, cloth, fabric, occasion, preparation time
            productCloth.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_fabrics_cloth_array)
                )
            )
            productFabric.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_fabrics_design_array)
                )
            )
            productOccasion.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_clothing_occasion_types_array)
                )
            )
            preparationTime.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.preparation_time_array)
                )
            )

            gender.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.gender_array)
                )
            )

            // Defaults to Male selection.
            gender.setText(gender.adapter.getItem(0).toString(), false)

            // Launch PaletteBottomSheet for picking a color.
            colorCard.setOnClickListener {
                findNavController().navigate(
                    NewProductFragmentDirections.actionNewProductFragmentToBottomSheetPalette(
                        showColorsForJewellery = false
                    )
                )
            }

            // Try fetching latest boutique category dropdown items.
            newProductViewModel.verifyAndFetchBoutiqueCategoryDropDownItems()

            newProductViewModel.getBoutiqueCategoryDropDownItems()
                .observe(viewLifecycleOwner) { dropDownItems ->
                    if (dropDownItems.first && dropDownItems.second.isNotEmpty())
                        productCategory.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                dropDownItems.second
                            )
                        )
                    else
                        productCategory.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.boutique_category_type_array)
                            )
                        )
                }

            // Setup observers for invalid form data
            newProductViewModel.getIsProductNameEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(productName)
            }

            newProductViewModel.getIsProductTypeEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productCategory)
            }

            newProductViewModel.getIsProductOccasionEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productOccasion)
            }

            newProductViewModel.getIsProductFabricEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productFabric)
            }

            newProductViewModel.getIsPreparationTimeEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(preparationTime)
            }

            newProductViewModel.getIsProductStoryEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(productStory)
            }

            newProductViewModel.getIsProductClothEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productCloth)
            }

            newProductViewModel.getIsProductDescriptionEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(productDescription)
            }

            newProductViewModel.getIsProductColorEmpty().observe(viewLifecycleOwner) {
                if (it) {
                    submitProduct.isEnabled = true

                    // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        "isSubmissionDone",
                        false
                    )

                    Snackbar.make(
                        view,
                        getString(R.string.choose_a_color),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsStartPriceZero().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(startPrice)
            }

            newProductViewModel.getIsEndPriceZero().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(endPrice)
            }

            newProductViewModel.getIsProductImage1LengthZero().observe(viewLifecycleOwner) {
                if (it) {
                    submitProduct.isEnabled = true
                    // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        "isSubmissionDone",
                        false
                    )
                    Snackbar.make(
                        view,
                        getString(R.string.min_image_upload_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsProductImage2LengthZero().observe(viewLifecycleOwner) {
                if (it) {
                    submitProduct.isEnabled = true
                    // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        "isSubmissionDone",
                        false
                    )
                    Snackbar.make(
                        view,
                        getString(R.string.min_image_upload_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsProductSubmissionSuccessful().observe(viewLifecycleOwner) {
                // Enable the submit product button
                binding.submitProduct.isEnabled = true

                // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    "isSubmissionDone",
                    it
                )

                if (it) {
                    productsViewModel.getProducts(forceRefresh = true)
                    findNavController().navigateUp()
                    Snackbar.make(
                        view,
                        getString(R.string.product_submitted_successfully),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        Handler().postDelayed({
                            setAnchorView(R.id.fab)
                            show()
                        }, 300)
                    }
                } else {
                    Snackbar.make(
                        view,
                        getString(R.string.some_error_occured),
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        setAction(getString(R.string.retry)) { onSubmitButtonClick(colorName) }
                        show()
                    }
                }
            }

            newProductViewModel.getIsSketchSubmissionSuccessful().observe(viewLifecycleOwner) {
                // Enable the submit product button
                binding.submitProduct.isEnabled = true

                // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    "isSubmissionDone",
                    it
                )

                if (it) {
                    productsViewModel.getSketches(forceRefresh = true)
                    findNavController().navigateUp()
                    Snackbar.make(
                        view,
                        getString(R.string.product_submitted_successfully),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        Handler().postDelayed({
                            setAnchorView(R.id.fab)
                            show()
                        }, 300)
                    }
                } else {
                    Snackbar.make(
                        view,
                        getString(R.string.some_error_occured),
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        setAction(getString(R.string.retry)) { onSubmitButtonClick(colorName) }
                        show()
                    }
                }
            }

            // Image click listeners.
            productImage1.setOnClickListener { choosePhoto(0) }
            productImage2.setOnClickListener { choosePhoto(1) }
            productImage3.setOnClickListener { choosePhoto(2) }
            productImage4.setOnClickListener { choosePhoto(3) }
            productImage5.setOnClickListener { choosePhoto(4) }

            // Final submit button click listener.
            submitProduct.setOnClickListener {
                onSubmitButtonClick(colorName)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE)
            data?.let { intentData ->
                lifecycleScope.launch {
                    handleSelectedImages(intentData)
                }
            }
    }

    /**
     * Handle when the submitProduct button or Retry action in Snackbar is clicked
     */
    private fun onSubmitButtonClick(colorName: String) {
        binding.submitProduct.isEnabled = false
        lifecycleScope.launch {
            binding.apply {
                profileViewModel.getRetailer().value?.let {
                    findNavController().navigate(NewProductFragmentDirections.actionNewProductFragmentToProductPostingBottomSheetDialog())
                    if (it.businessCategory == "B")
                        newProductViewModel.submitProduct(
                            productName = productName.text.toString(),
                            productType = productCategory.text.toString(),
                            productCloth = productCloth.text.toString(),
                            productFabric = productFabric.text.toString(),
                            productOccasion = productOccasion.text.toString(),
                            preparationTime = preparationTime.text.toString(),
                            productColor = colorName,
                            gender = gender.text.toString(),
                            productDescription = productDescription.text.toString(),
                            startPrice = startPrice.text.toString(),
                            endPrice = endPrice.text.toString(),
                            productImage1 = imageFiles[0],
                            productImage2 = imageFiles[1],
                            productImage3 = imageFiles[2],
                            productImage4 = imageFiles[3],
                            productImage5 = imageFiles[4],
                            listener = this@NewProductFragment
                        )
                    else // Sketch
                        newProductViewModel.submitSketch(
                            productName = productName.text.toString(),
                            productDescription = productDescription.text.toString(),
                            productStory = productStory.text.toString(),
                            productImage1 = imageFiles[0],
                            productImage2 = imageFiles[1],
                            productImage3 = imageFiles[2],
                            productImage4 = imageFiles[3],
                            productImage5 = imageFiles[4],
                            listener = this@NewProductFragment
                        )
                }
            }
        }
    }

    /**
     * Focus on the field that requires input from the user.
     * @param textInputEditText: EditText for being focused.
     */
    private fun requestFocusForEditText(textInputEditText: TextInputEditText) {
        binding.submitProduct.isEnabled = true
        textInputEditText.requestFocus()

        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }

    /**
     * Focus on the field that requires input from the user.
     * @param materialAutoCompleteTextView: AutoCompleteTextView for being focused.
     */
    private fun setEmptyDropDownError(materialAutoCompleteTextView: MaterialAutoCompleteTextView) {
        binding.submitProduct.isEnabled = true
        materialAutoCompleteTextView.requestFocus()

        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }

    /**
     * Launch an intent for picking an image from device storage.
     * @param currentImageSelectionIndex: Index of chosen image.
     */
    private fun choosePhoto(currentImageSelectionIndex: Int) {
        this.currentImageSelectionIndex = currentImageSelectionIndex

        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_image)),
            REQUEST_IMAGE
        )
    }

    override fun onProgressUpdate(percentage: Int) {
        mainActivityViewModel.setUploadProgress(progress = percentage)
    }

    override fun onError(e: Exception) {
        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }

    private suspend fun handleSelectedImages(selectedData: Intent) = withContext(Dispatchers.IO) {
        selectedData.clipData?.let { clipData ->
            if (clipData.itemCount > 5) {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.max_images_snackbar_msg),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return@withContext
            }

            // Track maxSelectionsLeft.
            var maxSelectionsLeft = 0
            maxSelectionsLeft =
                if (imageFiles[0] == null) maxSelectionsLeft + 1 else maxSelectionsLeft
            maxSelectionsLeft =
                if (imageFiles[1] == null) maxSelectionsLeft + 1 else maxSelectionsLeft
            maxSelectionsLeft =
                if (imageFiles[2] == null) maxSelectionsLeft + 1 else maxSelectionsLeft
            maxSelectionsLeft =
                if (imageFiles[3] == null) maxSelectionsLeft + 1 else maxSelectionsLeft
            maxSelectionsLeft =
                if (imageFiles[4] == null) maxSelectionsLeft + 1 else maxSelectionsLeft

            // Verify if user selects more than maxSelectionsLeft.
            if (clipData.itemCount > maxSelectionsLeft && clipData.itemCount != 5 && maxSelectionsLeft != 0) {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.only_x_selections_left_snackbar_msg, maxSelectionsLeft),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return@withContext
            }

            val firstNullIndex =
                if (imageFiles[0] == null) 0 else if (imageFiles[1] == null) 1 else if (imageFiles[2] == null) 2 else if (imageFiles[3] == null) 3 else if (imageFiles[4] == null) 4 else 0

            for (i in 0 until clipData.itemCount) {
                // Get Uri of selected image and create a file at cache dir.
                val imageUri = clipData.getItemAt(i).uri
                imageFiles[i] = File(
                    requireContext().cacheDir,
                    "image$i.${FileUtils.getExtension(requireContext(), imageUri!!)}"
                )

                // Display image in imageView.
                requireActivity().runOnUiThread {
                    imageMap[i]!!.setImageURI(imageUri)
                }

                // Copy input stream to file.
                FileUtils.copyInputStreamToFile(
                    requireContext().contentResolver.openInputStream(
                        imageUri
                    )!!, imageFiles[i]
                )
            }
        } ?: selectedData.data?.let { uri ->
            imageFiles[currentImageSelectionIndex] = File(
                requireContext().cacheDir,
                "image${currentImageSelectionIndex + 1}.${
                    FileUtils.getExtension(
                        requireContext(),
                        uri
                    )
                }"
            )
            FileUtils.copyInputStreamToFile(
                requireContext().contentResolver.openInputStream(
                    uri
                )!!, imageFiles[currentImageSelectionIndex]
            )

            requireActivity().runOnUiThread {
                imageMap[currentImageSelectionIndex]!!.setImageURI(uri)
            }
        }
    }
}