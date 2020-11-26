package `in`.trendition.ui.store.newproduct

import `in`.trendition.R
import `in`.trendition.databinding.FragmentStoreNewProductBinding
import `in`.trendition.ui.MainActivityViewModel
import `in`.trendition.ui.newproduct.NewProductViewModel
import `in`.trendition.ui.product.ProductsViewModel
import `in`.trendition.util.FileUtils
import `in`.trendition.util.ProgressUpload
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
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
class StoreNewProductFragment : Fragment(), ProgressUpload.UploadListener {
    private lateinit var binding: FragmentStoreNewProductBinding
    private var imageFiles = ArrayList<File?>().apply {
        // Assign initial values to 5 images
        add(null)
        add(null)
        add(null)
        add(null)
        add(null)
    }

    private val disableBackAction: OnBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                // Do nothing
            }
        }

    @Inject
    lateinit var newProductViewModel: NewProductViewModel

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, disableBackAction)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreNewProductBinding.inflate(inflater)
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
                    binding.colorNameText.apply {
                        text = colorName
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.apply {
            // Setup same height and width of ImageView
            productImage1.post { productImage1.layoutParams.height = productImage1.width }

            // Setup adapters for category, cloth, fabric, occasion, preparation time
            productCategory.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_categories)
                )
            )
            setPiece.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_dress_materials_set_piece)
                )
            )
            cloth.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_fabrics_cloth_array)
                )
            )
            design.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_fabrics_design_array)
                )
            )

            deliveryTime.setAdapter(
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

            pattern.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_fabrics_pattern_array)
                )
            )

            productOccasion.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.store_clothing_occasion_types_array)
                )
            )

            productType.setOnItemClickListener { parent, view, position, id ->
                areSizesAvailable =
                    !(productType.text.toString() == "Dupatta" || productType.text.toString() == "Sarees" || productType.text.toString() == "Shawl/Stoles")
            }

            // Default setPiecePosition = 0
            setPiecePosition = 0
            setPiece.setText(setPiece.adapter.getItem(0).toString(), false)
            setPiece.setOnItemClickListener { _, _, position, _ ->
                setPiecePosition = position
            }

            // Observe gender item click listener to populate adapters
            // Position: 0 - Male, 1 - Female
            gender.setOnItemClickListener { _, _, position, _ ->
                productType.setText("", false)
                areSizesAvailable = false
                if (productCategory.text.toString() == "Jewellery")
                    productType.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(if (position == 0) R.array.store_jewellery_men_types_array else R.array.store_jewellery_women_types_array)
                        )
                    )
                else if (productCategory.text.toString() == "Clothing")
                    productType.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            resources.getStringArray(if (position == 0) R.array.store_clothing_men_types_array else R.array.store_clothing_women_types_array)
                        )
                    )
            }

            // Launch PaletteBottomSheet for picking a color.
            colorPalette.setOnClickListener {
                findNavController().navigate(
                    StoreNewProductFragmentDirections.actionFabricNewProductFragmentToBottomSheetPalette(
                        showColorsForJewellery = if (productCategory.text.isEmpty()) false else productCategory.text.toString() == "Jewellery"
                    )
                )
            }

            // Setup observers for invalid form data
            newProductViewModel.getIsProductTypeEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productType)
            }

            newProductViewModel.getIsProductFabricEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(design)
            }

            newProductViewModel.getIsProductClothEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(cloth)
            }

            newProductViewModel.getIsProductPatternEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(pattern)
            }

            newProductViewModel.getIsSetPieceEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(setPiece)
            }

            newProductViewModel.getIsTopMeasurementEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(topMeasurement)
            }

            newProductViewModel.getIsBottomMeasurementEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(bottomMeasurement)
            }

            newProductViewModel.getIsDupattaMeasurementEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(dupattaMeasurement)
            }

            newProductViewModel.getIsWeightEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(weight)
            }

            newProductViewModel.getIsGenderEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(gender)
            }

            newProductViewModel.getIsProductOccasionEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(productOccasion)
            }

            newProductViewModel.getIsProductMaterialEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(pattern)
            }

            // Listen for category changes and update hint in quantity (TextInputEditText)
            // Also hide/display views based on category
            // 0 - Fabric, 1 - Dress Material, 2 - Clothing, 3 - Jewellery
            productCategory.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> {
                        isFabricsSelected = true
                        isClothingSelected = false
                        isDressMaterialsSelected = false
                        isJewellerySelected = false
                    }
                    1 -> {
                        isDressMaterialsSelected = true
                        isFabricsSelected = false
                        isClothingSelected = false
                        isJewellerySelected = false
                    }
                    2 -> {
                        gender.setText(gender.adapter.getItem(0).toString(), false)
                        productType.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.store_clothing_men_types_array)
                            )
                        )
                        isClothingSelected = true
                        isFabricsSelected = false
                        isDressMaterialsSelected = false
                        isJewellerySelected = false
                    }
                    else -> {
                        gender.setText(gender.adapter.getItem(0).toString(), false)
                        productType.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.store_jewellery_men_types_array)
                            )
                        )
                        isJewellerySelected = true
                        isFabricsSelected = false
                        isClothingSelected = false
                        isDressMaterialsSelected = false
                    }
                }
            }

            // Set Fabric as default category in dropdown
            productCategory.setText(productCategory.adapter.getItem(0).toString(), false)
            isFabricsSelected = true
            isClothingSelected = false
            isDressMaterialsSelected = false
            isJewellerySelected = false

            newProductViewModel.getIsProductNameEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(productName)
            }

            newProductViewModel.getIsQuantityEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(availableQuantity)
            }

            newProductViewModel.getIsDeliveryTimeEmpty().observe(viewLifecycleOwner) {
                if (it)
                    setEmptyDropDownError(deliveryTime)
            }

            newProductViewModel.getIsProductDescriptionEmpty().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(description)
            }

            newProductViewModel.getIsProductColorEmpty().observe(viewLifecycleOwner) {
                if (it) {
                    Snackbar.make(
                        view,
                        getString(R.string.choose_a_color),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    // Enable submit product button
                    submitProduct.isEnabled = true
                }
            }

            newProductViewModel.getIsProductPriceEmpty().observe(viewLifecycleOwner) {
                if (it)
                    Snackbar.make(
                        view,
                        getString(R.string.price_cannot_be_zero),
                        Snackbar.LENGTH_SHORT
                    ).show()
            }

            newProductViewModel.getIsProductImage1LengthZero().observe(viewLifecycleOwner) {
                if (it) {
                    binding.submitProduct.isEnabled = true
                    Snackbar.make(
                        view,
                        getString(R.string.min_image_upload_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getNoSizesAvailable().observe(viewLifecycleOwner) {
                if (it) {
                    submitProduct.isEnabled = true
                    Snackbar.make(
                        view,
                        getString(R.string.choose_atleast_one_size),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsProductImage2LengthZero().observe(viewLifecycleOwner) {
                if (it) {
                    binding.submitProduct.isEnabled = true
                    Snackbar.make(
                        view,
                        getString(R.string.min_image_upload_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsStoreSubmissionSuccessful().observe(viewLifecycleOwner) {
                // Enable the submit product button
                binding.submitProduct.isEnabled = true
                disableBackAction.isEnabled = false

                // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    "isSubmissionDone",
                    it
                )
                if (it) {
                    productsViewModel.getStore(forceRefresh = true)
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
                        setAction(getString(R.string.retry)) {
                            onSubmitButtonClick(colorName)
                        }
                        show()
                    }
                }
            }

            // Image click listeners.
            productImage1.setOnClickListener { choosePhoto(requestCode = 1) }
            productImage2.setOnClickListener { choosePhoto(requestCode = 2) }
            productImage3.setOnClickListener { choosePhoto(requestCode = 3) }
            productImage4.setOnClickListener { choosePhoto(requestCode = 4) }
            productImage5.setOnClickListener { choosePhoto(requestCode = 5) }

            // Final submit button click listener.
            submitProduct.setOnClickListener {
                onSubmitButtonClick(colorName)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 0)
            return
        val imageUri = data?.data
        imageUri?.let {
            binding.apply {
                lifecycleScope.launch {
                    when (requestCode) {
                        1 -> handleSelectedImage(
                            productImage1,
                            it,
                            "image1",
                            requestCode
                        )
                        2 -> handleSelectedImage(
                            productImage2,
                            it,
                            "image2",
                            requestCode
                        )
                        3 -> handleSelectedImage(
                            productImage3,
                            it,
                            "image3",
                            requestCode
                        )
                        4 -> handleSelectedImage(
                            productImage4,
                            it,
                            "image4",
                            requestCode
                        )
                        5 -> handleSelectedImage(
                            productImage5,
                            it,
                            "image5",
                            requestCode
                        )
                        else -> throw RuntimeException("Unknown request code - $requestCode when picking an image")
                    }
                }
            }
        }
    }

    /**
     * Handle when the submitProduct button or Retry action in Snackbar is clicked
     */
    private fun onSubmitButtonClick(colorName: String) {
        binding.run {
            if (productCategory.text.isEmpty()) {
                setEmptyDropDownError(productCategory)
                return@run
            }
            submitProduct.isEnabled = false
            disableBackAction.isEnabled = true

            findNavController().navigate(StoreNewProductFragmentDirections.actionStoreNewProductFragmentToProductPostingBottomSheetDialog())

            lifecycleScope.launch {
                when {
                    productCategory.text.toString() == "Fabric" -> newProductViewModel.submitFabric(
                        productName = productName.text.toString(),
                        productType = "Fabric",
                        productCloth = cloth.text.toString(),
                        productFabric = design.text.toString(),
                        deliveryTime = deliveryTime.text.toString(),
                        productQuantity = availableQuantity.text.toString(),
                        productColor = colorName,
                        productDescription = description.text.toString(),
                        productImage1 = imageFiles[0],
                        productImage2 = imageFiles[1],
                        productImage3 = imageFiles[2],
                        productImage4 = imageFiles[3],
                        productImage5 = imageFiles[4],
                        productPrice = price.text.toString(),
                        productPattern = pattern.text.toString(),
                        productWeight = weight.text.toString(),
                        productWidth = width.text.toString(),
                        minimumQuantity = minimumQuantity.text.toString(),
                        listener = this@StoreNewProductFragment
                    )
                    productCategory.text.toString() == "Clothing" -> newProductViewModel.submitCloth(
                        productName = productName.text.toString(),
                        productType = productType.text.toString(),
                        productCloth = cloth.text.toString(),
                        productFabric = design.text.toString(),
                        deliveryTime = deliveryTime.text.toString(),
                        productQuantity = availableQuantity.text.toString(),
                        productColor = colorName,
                        productDescription = description.text.toString(),
                        productImage1 = imageFiles[0],
                        productImage2 = imageFiles[1],
                        productImage3 = imageFiles[2],
                        productImage4 = imageFiles[3],
                        productImage5 = imageFiles[4],
                        productPrice = price.text.toString(),
                        productOccasion = productOccasion.text.toString(),
                        isSAvailable = checkboxS.isChecked,
                        isMAvailable = checkboxM.isChecked,
                        isLAvailable = checkboxL.isChecked,
                        isXLAvailable = checkboxXl.isChecked,
                        isXXLAvailable = checkboxXxl.isChecked,
                        gender = gender.text.toString(),
                        listener = this@StoreNewProductFragment
                    )
                    productCategory.text.toString() == "Dress Material" -> newProductViewModel.submitDressMaterial(
                        productName = productName.text.toString(),
                        productType = "Dress Material",
                        productCloth = cloth.text.toString(),
                        productFabric = design.text.toString(),
                        deliveryTime = deliveryTime.text.toString(),
                        productQuantity = availableQuantity.text.toString(),
                        productColor = colorName,
                        productDescription = description.text.toString(),
                        productImage1 = imageFiles[0],
                        productImage2 = imageFiles[1],
                        productImage3 = imageFiles[2],
                        productImage4 = imageFiles[3],
                        productImage5 = imageFiles[4],
                        productPrice = price.text.toString(),
                        setPiece = setPiece.text.toString(),
                        topMeasurement = topMeasurement.text.toString(),
                        bottomMeasurement = bottomMeasurement.text.toString(),
                        dupattaMeasurement = dupattaMeasurement.text.toString(),
                        setPiecePosition = setPiecePosition!!,
                        productWeight = weight.text.toString(),
                        productPattern = pattern.text.toString(),
                        listener = this@StoreNewProductFragment
                    )
                    else -> newProductViewModel.submitJewellery(
                        productName = productName.text.toString(),
                        productType = productType.text.toString(),
                        deliveryTime = deliveryTime.text.toString(),
                        productQuantity = availableQuantity.text.toString(),
                        productColor = colorName,
                        productDescription = description.text.toString(),
                        productImage1 = imageFiles[0],
                        productImage2 = imageFiles[1],
                        productImage3 = imageFiles[2],
                        productImage4 = imageFiles[3],
                        productImage5 = imageFiles[4],
                        productPrice = price.text.toString(),
                        gender = gender.text.toString(),
                        productMaterial = productMaterial.text.toString(),
                        listener = this@StoreNewProductFragment
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
        if (!binding.submitProduct.isEnabled)
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
        if (!binding.submitProduct.isEnabled)
            binding.submitProduct.isEnabled = true
        materialAutoCompleteTextView.requestFocus()

        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }

    /**
     * Launch an intent for picking an image from device storage.
     * @param requestCode: Request code for onActivityResult.
     */
    private fun choosePhoto(requestCode: Int) {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_image)),
            requestCode
        )
    }

    /**
     * Handle the selected image. It does 2 things:
     * 1. Set the selected image as a preview in the ImageView using Uri.
     * 2. Copy the selected Uri stream into File for uploading
     * @param imageView: The target for preview.
     * @param imageUri: Source Uri for the selected file.
     * @param imageNameWithoutExt: Extension of the image file.
     * @param requestCode: Request code used when selecting an image. Here, it's used as position for access nth file in [imageFiles]
     */
    private suspend fun handleSelectedImage(
        imageView: ImageView,
        imageUri: Uri?,
        imageNameWithoutExt: String,
        requestCode: Int
    ) = withContext(Dispatchers.IO) {
        requireActivity().runOnUiThread {
            imageView.setImageURI(imageUri)
        }
        imageFiles[requestCode - 1] = File(
            requireContext().cacheDir,
            "$imageNameWithoutExt.${FileUtils.getExtension(requireContext(), imageUri!!)}"
        )
        FileUtils.copyInputStreamToFile(
            requireContext().contentResolver.openInputStream(
                imageUri
            )!!, imageFiles[requestCode - 1]
        )
    }

    override fun onProgressUpdate(percentage: Int) {
        mainActivityViewModel.setUploadProgress(progress = percentage)
    }

    override fun onError(e: Exception) {
        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }
}