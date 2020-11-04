package `in`.trendition.ui.newproduct

import `in`.trendition.R
import `in`.trendition.databinding.FragmentNewProductBinding
import `in`.trendition.ui.product.ProductsViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import `in`.trendition.util.FileUtils.copyInputStreamToFile
import `in`.trendition.util.FileUtils.getExtension
import android.app.Activity
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class NewProductFragment : Fragment() {
    private lateinit var binding: FragmentNewProductBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    binding.colorNameText.apply {
                        text = colorName
                        visibility = View.VISIBLE
                    }
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
            productCategory.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.boutique_category_type_array)
                )
            )
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

            // Launch PaletteBottomSheet for picking a color.
            colorPalette.setOnClickListener {
                findNavController().navigate(
                    NewProductFragmentDirections.actionNewProductFragmentToBottomSheetPalette(
                        showColorsForJewellery = false
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
        if (resultCode == Activity.RESULT_CANCELED)
            return
        val imageUri = data?.data
        imageUri?.let {
            binding.apply {
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

    /**
     * Handle when the submitProduct button or Retry action in Snackbar is clicked
     */
    private fun onSubmitButtonClick(colorName: String) {
        binding.submitProduct.isEnabled = false
        lifecycleScope.launch {
            binding.apply {
                profileViewModel.getRetailer().value?.let {
                    if (it.businessCategory == "B")
                        newProductViewModel.submitProduct(
                            productName = productName.text.toString(),
                            productType = productCategory.text.toString(),
                            productCloth = productCloth.text.toString(),
                            productFabric = productFabric.text.toString(),
                            productOccasion = productOccasion.text.toString(),
                            preparationTime = preparationTime.text.toString(),
                            productColor = colorName,
                            productDescription = productDescription.text.toString(),
                            startPrice = startPrice.text.toString(),
                            endPrice = endPrice.text.toString(),
                            productImage1 = imageFiles[0],
                            productImage2 = imageFiles[1],
                            productImage3 = imageFiles[2],
                            productImage4 = imageFiles[3],
                            productImage5 = imageFiles[4]
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
                            productImage5 = imageFiles[4]
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
    }

    /**
     * Focus on the field that requires input from the user.
     * @param materialAutoCompleteTextView: AutoCompleteTextView for being focused.
     */
    private fun setEmptyDropDownError(materialAutoCompleteTextView: MaterialAutoCompleteTextView) {
        binding.submitProduct.isEnabled = true
        materialAutoCompleteTextView.requestFocus()
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
    private fun handleSelectedImage(
        imageView: ImageView,
        imageUri: Uri?,
        imageNameWithoutExt: String,
        requestCode: Int
    ) {
        imageView.setImageURI(imageUri)
        imageFiles[requestCode - 1] = File(
            requireContext().cacheDir,
            "$imageNameWithoutExt.${getExtension(requireContext(), imageUri!!)}"
        )
        copyInputStreamToFile(
            requireContext().contentResolver.openInputStream(
                imageUri
            )!!, imageFiles[requestCode - 1]
        )
    }
}