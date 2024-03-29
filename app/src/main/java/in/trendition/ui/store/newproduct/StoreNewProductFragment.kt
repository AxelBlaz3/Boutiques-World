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
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
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
class StoreNewProductFragment : Fragment(), ProgressUpload.UploadListener {
    private lateinit var binding: FragmentStoreNewProductBinding
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
    ): View {
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
            // Position: 0 - Male, 1 - Female, 2 - Unisex.
            gender.setOnItemClickListener { _, _, position, _ ->
                productType.setText("", false)
                areSizesAvailable = false
                if (productCategory.text.toString() == "Jewellery") {
                    newProductViewModel.getJewelleryTypeDropDownItems()
                        .observe(viewLifecycleOwner) { dropDownItems ->
                            if (dropDownItems.first)
                                productType.setAdapter(
                                    ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        if (position == 0) dropDownItems.second["Men"]!!.toMutableList() else if (position == 1) dropDownItems.second["Women"]!!.toMutableList() else dropDownItems.second["Unisex"]!!.toMutableList()
                                    )
                                )
                            else
                                productType.setAdapter(
                                    ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        resources.getStringArray(if (position == 0) R.array.store_jewellery_men_types_array else R.array.store_jewellery_women_types_array)
                                    )
                                )
                        }
                } else if (productCategory.text.toString() == "Clothing")
                    newProductViewModel.getClothingTypeDropDownItems()
                        .observe(viewLifecycleOwner) { dropDownItems ->
                            if (dropDownItems.first)
                                productType.setAdapter(
                                    ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        if (position == 0) dropDownItems.second["Men"]!!.toMutableList() else if (position == 1) dropDownItems.second["Women"]!!.toMutableList() else dropDownItems.second["Unisex"]!!.toMutableList()
                                    )
                                )
                            else
                                productType.setAdapter(
                                    ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        resources.getStringArray(if (position == 0) R.array.store_clothing_men_types_array else R.array.store_clothing_women_types_array)
                                    )
                                )
                        }
            }

            // Launch PaletteBottomSheet for picking a color.
            colorCard.setOnClickListener {
                findNavController().navigate(
                    StoreNewProductFragmentDirections.actionFabricNewProductFragmentToBottomSheetPalette(
                        showColorsForJewellery = if (productCategory.text.isEmpty()) false else productCategory.text.toString() == "Jewellery"
                    )
                )
            }

            // Fetch design and cloth dropdown items as they are common for every category.
            newProductViewModel.run {
                verifyAndFetchDesignDropDownItems()
                verifyAndFetchClothDropDownItems()
            }
            // Observe for latest dropdown items.
            newProductViewModel.getDesignDropDownItems()
                .observe(viewLifecycleOwner) { dropDownItems ->
                    if (dropDownItems.first && dropDownItems.second.isNotEmpty())
                        design.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                dropDownItems.second
                            )
                        )
                    else
                        design.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.store_fabrics_design_array)
                            )
                        )
                }

            newProductViewModel.getClothDropDownItems()
                .observe(viewLifecycleOwner) { dropDownItems ->
                    if (dropDownItems.first && dropDownItems.second.isNotEmpty())
                        cloth.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                dropDownItems.second
                            )
                        )
                    else
                        cloth.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                resources.getStringArray(R.array.store_fabrics_cloth_array)
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
            // Also hide/display views and fetch dropdown items based on category.
            // 0 - Fabric, 1 - Dress Material, 2 - Clothing, 3 - Jewellery
            productCategory.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> {
                        isFabricsSelected = true
                        isClothingSelected = false
                        isDressMaterialsSelected = false
                        isJewellerySelected = false

                        newProductViewModel.run {
                            verifyAndFetchDesignDropDownItems()
                            verifyAndFetchClothDropDownItems()
                        }
                    }
                    1 -> {
                        gender.setText(gender.adapter.getItem(0).toString(), false)
                        isDressMaterialsSelected = true
                        isFabricsSelected = false
                        isClothingSelected = false
                        isJewellerySelected = false

                        newProductViewModel.run {
                            verifyAndFetchDesignDropDownItems()
                            verifyAndFetchClothDropDownItems()
                        }
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

                        newProductViewModel.run {
                            verifyAndFetchDesignDropDownItems()
                            verifyAndFetchClothDropDownItems()
                            verifyAndFetchClothingTypeDropDownItems()
                        }
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

                        newProductViewModel.run {
                            verifyAndFetchDesignDropDownItems()
                            verifyAndFetchClothDropDownItems()
                            verifyAndFetchJewelleryTypeDropDownItems()
                        }
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
                    binding.submitProduct.isEnabled = true
                    disableBackAction.isEnabled = true
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

            newProductViewModel.getIsProductPriceEmpty().observe(viewLifecycleOwner) {
                if (it) {
                    disableBackAction.isEnabled = true
                    disableBackAction.isEnabled = true
                    // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        "isSubmissionDone",
                        false
                    )
                    Snackbar.make(
                        view,
                        getString(R.string.price_cannot_be_zero),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            newProductViewModel.getIsProductImage1LengthZero().observe(viewLifecycleOwner) {
                if (it) {
                    binding.submitProduct.isEnabled = true
                    disableBackAction.isEnabled = true
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

            newProductViewModel.getNoSizesAvailable().observe(viewLifecycleOwner) {
                if (it) {
                    binding.submitProduct.isEnabled = true
                    disableBackAction.isEnabled = true
                    // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        "isSubmissionDone",
                        false
                    )
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
                    disableBackAction.isEnabled = true
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
     * Handle when the submitProduct button or Retry action in Snackbar is clicked.
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
                        gender = gender.text.toString(),
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
        disableBackAction.isEnabled = true
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
        disableBackAction.isEnabled = true
        materialAutoCompleteTextView.requestFocus()

        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }

    /**
     * Launch an intent for picking an image from device storage.
     * @param currentImageSelectionIndex: Index of currently selected image.
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

            for (i in firstNullIndex until clipData.itemCount) {
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
            requireActivity().runOnUiThread {
                imageMap[currentImageSelectionIndex]!!.setImageURI(uri)
            }

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
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        mainActivityViewModel.setUploadProgress(progress = percentage)
    }

    override fun onError(e: Exception) {
        // Set isSubmissionDone for closing the ProductPostingBottomSheetDialog
        findNavController().currentBackStackEntry?.savedStateHandle?.set("isSubmissionDone", false)
    }
}