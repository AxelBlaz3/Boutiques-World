package com.boutiquesworld.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Palette
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.network.BoutiqueService
import com.boutiquesworld.repository.ProfileRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for managing the NewProduct submission.
 */
@FragmentScoped
class NewProductViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    private val boutiqueService: BoutiqueService
) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> =
        profileRepository.getRetailerMutableLiveData()
    private val isProductNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductTypeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductClothEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductFabricEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isGenderEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isSetPieceEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isDupattaMeasurementEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isBottomMeasurementEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isTopMeasurementEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val noSizesAvailable: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductPatternEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductMaterialEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductWidthEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductWeightEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isMinimumQuantityEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductOccasionEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isPreparationTimeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductColorEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductDescriptionEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductImage1LengthZero: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductImage2LengthZero: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isStartPriceZero: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isBusinessIdEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isBusinessNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isUuidEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isZoneEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isDeliveryTimeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isQuantityEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductPriceEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductStoryEmpty: MutableLiveData<Boolean> = MutableLiveData(false)

    // Know if the product submission is successful
    private val isProductSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    private val isStoreSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    private val isSketchSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    // RequestBody PartMap for BoutiqueService
    private val formDataMap = HashMap<String, RequestBody>()

    // List of MultipartBody.Part for image files
    private val imageFiles = ArrayList<MultipartBody.Part>()

    fun getIsProductNameEmpty(): LiveData<Boolean> = isProductNameEmpty
    fun getIsProductTypeEmpty(): LiveData<Boolean> = isProductTypeEmpty
    fun getIsProductClothEmpty(): LiveData<Boolean> = isProductClothEmpty
    fun getIsProductFabricEmpty(): LiveData<Boolean> = isProductFabricEmpty
    fun getIsProductOccasionEmpty(): LiveData<Boolean> = isProductOccasionEmpty
    fun getIsPreparationTimeEmpty(): LiveData<Boolean> = isPreparationTimeEmpty
    fun getIsProductColorEmpty(): LiveData<Boolean> = isProductColorEmpty
    fun getIsProductDescriptionEmpty(): LiveData<Boolean> = isProductDescriptionEmpty
    fun getIsProductImage1LengthZero(): LiveData<Boolean> = isProductImage1LengthZero
    fun getIsProductImage2LengthZero(): LiveData<Boolean> = isProductImage2LengthZero
    fun getIsStartPriceZero(): LiveData<Boolean> = isStartPriceZero
    fun getIsProductStoryEmpty(): LiveData<Boolean> = isProductStoryEmpty
    fun getIsProductSubmissionSuccessful(): LiveData<Boolean> = isProductSubmissionSuccessful
    fun getIsSketchSubmissionSuccessful(): LiveData<Boolean> = isSketchSubmissionSuccessful
    fun getIsProductPatternEmpty(): LiveData<Boolean> = isProductPatternEmpty
    fun getIsSetPieceEmpty(): LiveData<Boolean> = isSetPieceEmpty
    fun getIsTopMeasurementEmpty(): LiveData<Boolean> = isTopMeasurementEmpty
    fun getIsBottomMeasurementEmpty(): LiveData<Boolean> = isBottomMeasurementEmpty
    fun getIsDupattaMeasurementEmpty(): LiveData<Boolean> = isDupattaMeasurementEmpty
    fun getIsWeightEmpty(): LiveData<Boolean> = isProductWeightEmpty
    fun getIsGenderEmpty(): LiveData<Boolean> = isGenderEmpty
    fun getIsProductMaterialEmpty(): LiveData<Boolean> = isProductMaterialEmpty
    fun getNoSizesAvailable(): LiveData<Boolean> = noSizesAvailable

    // Store
    fun getIsProductPriceEmpty(): LiveData<Boolean> = isProductPriceEmpty
    fun getIsDeliveryTimeEmpty(): LiveData<Boolean> = isDeliveryTimeEmpty
    fun getIsQuantityEmpty(): LiveData<Boolean> = isQuantityEmpty
    fun getIsStoreSubmissionSuccessful(): LiveData<Boolean> = isStoreSubmissionSuccessful

    // Palette
    val paletteColors by lazy {
        ArrayList<Palette>().apply {
            add(Palette("#5eb160", "Green"))
            add(Palette("#f1a9c4", "Pink"))
            add(Palette("#0074d9", "Blue"))
            add(Palette("#000000", "Black"))
            add(Palette("#3c4477", "Navy Blue"))
            add(Palette("#4b0082", "Indigo"))
            add(Palette("#d34b56", "Red"))
            add(Palette("#e8e6cf", "Beige"))
            add(Palette("#eadc32", "Yellow"))
            add(Palette("#f2f2f2", "Semi White"))
            add(Palette("#ffffff", "White"))
            add(Palette("#cc9c33", "Mustard"))
            add(Palette("#9fa8ab", "Grey"))
            add(Palette("#f28d20", "Orange"))
            add(Palette("#300000", "Maroon"))
            add(Palette("#ffe5b4", "Peach"))
            add(Palette("#ede6b9", "Cream"))
            add(Palette("#915039", "Brown"))
            add(Palette("#008080", "Teal"))
            add(Palette("#ff7f50", "Coral"))
            add(Palette("#800080", "Purple"))
            add(Palette("#2e8b57", "Sea Green"))
            add(Palette("#40e0d0", "Turquoise"))
            add(Palette("#3d9970", "Olive"))
            add(Palette("#b74c0e", "Rust"))
            add(Palette("#b9529f", "Magenta"))
            add(Palette("#36454f", "Charcoal"))
            add(Palette("#a03245", "Burgundy"))
            add(Palette("#5db653", "Lime Green"))
            add(Palette("#e5c74a", "Gold"))
            add(Palette("#483c32", "Taupe"))
            add(Palette("#4b302f", "Coffee Brown"))
            add(Palette("#e0b0ff", "Mauve"))
            add(Palette("#d6d6e5", "Lavender"))
            add(Palette("#b3b3b3", "Silver"))
            add(Palette("#c3b091", "Khaki"))
            add(Palette("#9fa8ab", "Grey Melange"))
            add(Palette("#8dc04a", "Fluorescence Green"))
            add(Palette("#dd2f86", "Rose"))
            add(Palette("#d2b48c", "Tan"))
            add(Palette("#aa6c39", "Copper"))
            add(Palette("#cc8240", "Bronze"))
            add(Palette("#b3b3b3", "Steel"))
            add(Palette("#e0d085", "Metallic"))
        }
    }

    val jewelleryPalette by lazy {
        ArrayList<Palette>().apply {
            add(Palette("#ffd7cc", "Gold"))
            add(Palette("#c0c0c0", "Silver"))
            add(Palette("#800000", "Maroon"))
            add(Palette("#008000", "Green"))
            add(Palette("#ffffff", "White"))
            add(Palette("#0074d9", "Blue"))
            add(Palette("#d34b56", "Red"))
            add(Palette("#000000", "Black"))
            add(Palette("#eadc32", "Yellow"))
            add(Palette("#ffc0cb", "Pink"))
            add(Palette("#f28d20", "Orange"))
            add(Palette("#ff00ff", "Magenta"))
            add(Palette("#800080", "Purple"))
            add(Palette("#915039", "Brown"))
            add(Palette("#40e0d0", "Turquoise"))
            add(Palette("#f5f5dc", "Beige"))
            add(Palette("#9fa8ab", "Grey"))
            add(Palette("#cc8240", "Bronze"))
            add(Palette("#800080", "Violet"))
            add(Palette("#aa6c39", "Copper"))
            add(Palette("#ede6b9", "Cream"))
        }
    }

    fun submitProduct(
        productType: String,
        productName: String,
        productCloth: String,
        productFabric: String,
        productOccasion: String,
        preparationTime: String,
        productColor: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        startPrice: Int,
        endPrice: Int
    ) {
        try {
            retailer.value?.let {
                val isDataValid = isProductDataValid(
                    productType,
                    productName,
                    productCloth,
                    productFabric,
                    productOccasion,
                    preparationTime,
                    productColor,
                    productDescription,
                    productImage1,
                    productImage2,
                    productImage3,
                    productImage4,
                    productImage5,
                    startPrice,
                    endPrice,
                    it.shopId.toString(),
                    it.businessName,
                    it.uuid,
                    it.zone
                )
                if (isDataValid) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val response =
                                boutiqueService.postProduct(formDataMap, imageFiles).execute()
                            if (response.isSuccessful) {
                                isProductSubmissionSuccessful.postValue(true)
                                productImage1?.delete()
                                productImage2?.delete()
                                productImage3?.delete()
                                productImage4?.delete()
                                productImage5?.delete()
                            } else
                                isProductSubmissionSuccessful.postValue(false)
                        } catch (e: Exception) {
                            isProductSubmissionSuccessful.postValue(false)
                            e.printStackTrace()
                        }
                    }
                } else
                    return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitSketch(
        productName: String,
        productStory: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            isProductNameEmpty.postValue(productName.isEmpty())
            isProductStoryEmpty.postValue(productStory.isEmpty())
            isProductDescriptionEmpty.postValue(productDescription.isEmpty())
            isProductImage1LengthZero.postValue(productImage1 == null)
            isProductImage2LengthZero.postValue(productImage2 == null)

            if (productName.isEmpty() || productStory.isEmpty() || productDescription.isEmpty() || productImage1 == null || productImage2 == null)
                return@launch

            formDataMap.apply {
                put("product_name", getFormRequestBody(productName))
                put("product_description", getFormRequestBody(productDescription))
                put("product_story", getFormRequestBody(productStory))
                retailer.value?.let {
                    put("business_id", getFormRequestBody(it.shopId.toString()))
                    put("uuid", getFormRequestBody(it.uuid))
                    put("business_name", getFormRequestBody(it.businessName))
                }
                put("product_status", getFormRequestBody("0"))
            }

            addFileMultiPartBody("product_image1", productImage1)
            addFileMultiPartBody("product_image2", productImage2)
            addFileMultiPartBody("product_image3", productImage3)
            addFileMultiPartBody("product_image4", productImage4)
            addFileMultiPartBody("product_image5", productImage5)

            try {
                val response = boutiqueService.postSketch(formDataMap, imageFiles).execute()
                if (response.code() == 201) {
                    isSketchSubmissionSuccessful.postValue(true)
                    return@launch
                } else {
                    isSketchSubmissionSuccessful.postValue(false)
                    return@launch
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isSketchSubmissionSuccessful.postValue(false)
        }
    }

    fun submitFabric(
        productType: String = "Fabric",
        productName: String,
        productCloth: String,
        productFabric: String,
        productPattern: String,
        productWidth: String,
        productWeight: String,
        productCategory: String = "Fabric",
        deliveryTime: String,
        productQuantity: String,
        minimumQuantity: String,
        productColor: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        productPrice: String
    ) {
        try {
            retailer.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    isProductTypeEmpty.postValue(productType.isEmpty())
                    isProductNameEmpty.postValue(productName.isEmpty())
                    isProductClothEmpty.postValue(productCloth.isEmpty())
                    isProductFabricEmpty.postValue(productFabric.isEmpty())
                    isProductPatternEmpty.postValue(productPattern.isEmpty())
                    isProductWidthEmpty.postValue(productWidth.isEmpty())
                    isProductWeightEmpty.postValue(productWeight.isEmpty())
                    isDeliveryTimeEmpty.postValue(deliveryTime.isEmpty())
                    isMinimumQuantityEmpty.postValue(minimumQuantity.isEmpty())
                    isQuantityEmpty.postValue(productQuantity.isEmpty())
                    isProductDescriptionEmpty.postValue(productDescription.isEmpty())
                    isProductColorEmpty.postValue(productColor.isEmpty())
                    isProductPriceEmpty.postValue(productPrice.isEmpty())
                    isProductImage1LengthZero.postValue(productImage1 == null)
                    isProductImage2LengthZero.postValue(productImage2 == null)

                    if (productType.isEmpty() ||
                        productName.isEmpty() ||
                        productCloth.isEmpty() ||
                        productFabric.isEmpty() ||
                        productPattern.isEmpty() ||
                        productWidth.isEmpty() ||
                        productWeight.isEmpty() ||
                        deliveryTime.isEmpty() ||
                        minimumQuantity.isEmpty() ||
                        productQuantity.isEmpty() ||
                        productColor.isEmpty() ||
                        productDescription.isEmpty() ||
                        productPrice.isEmpty() ||
                        productImage1 == null ||
                        productImage2 == null
                    )
                        return@launch

                    formDataMap.apply {
                        retailer.value?.let {
                            put("product_name", getFormRequestBody(productName))
                            put("product_category", getFormRequestBody(productCategory))
                            put("product_type", getFormRequestBody(productType))
                            put("product_description", getFormRequestBody(productDescription))
                            put("product_price", getFormRequestBody(productPrice))
                            put("product_colour", getFormRequestBody(productColor))
                            put("product_cloth", getFormRequestBody(productCloth))
                            put("product_fabric", getFormRequestBody(productFabric))
                            put("product_pattern", getFormRequestBody(productPattern))
                            put("width", getFormRequestBody(productWidth))
                            put("weight", getFormRequestBody(productWeight))
                            put("available_quantity", getFormRequestBody(productQuantity))
                            put("minimum_quantity", getFormRequestBody(minimumQuantity))
                            put("delivery_time", getFormRequestBody(deliveryTime))
                            put("business_category", getFormRequestBody(it.businessCategory))
                            put(
                                "business_category_type",
                                getFormRequestBody(it.businessCategoryType)
                            )
                            put("business_id", getFormRequestBody(it.shopId.toString()))
                            put("uuid", getFormRequestBody(it.uuid))
                            put("business_name", getFormRequestBody(it.businessName))
                            put("likes", getFormRequestBody("0"))
                            put("product_status", getFormRequestBody("0"))
                        }
                    }

                    addFileMultiPartBody("product_image1", productImage1)
                    addFileMultiPartBody("product_image2", productImage2)
                    if (productImage3 != null)
                        addFileMultiPartBody("product_image3", productImage3)
                    if (productImage4 != null)
                        addFileMultiPartBody("product_image4", productImage4)
                    if (productImage5 != null)
                        addFileMultiPartBody("product_image5", productImage5)
                    try {
                        val response =
                            boutiqueService.postStoreProduct(formDataMap, imageFiles).execute()
                        if (response.code() == 201) {
                            isStoreSubmissionSuccessful.postValue(true)
                            productImage1.delete()
                            productImage2.delete()
                            productImage3?.delete()
                            productImage4?.delete()
                            productImage5?.delete()
                        } else
                            isStoreSubmissionSuccessful.postValue(false)
                    } catch (e: Exception) {
                        isStoreSubmissionSuccessful.postValue(false)
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitDressMaterial(
        productName: String,
        productCategory: String = "Dress Material",
        productType: String = "Dress Material",
        setPiece: String,
        productDescription: String,
        productPrice: String,
        productColor: String,
        productCloth: String,
        productFabric: String,
        productPattern: String,
        productWeight: String,
        topMeasurement: String,
        bottomMeasurement: String,
        dupattaMeasurement: String,
        deliveryTime: String,
        productQuantity: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        setPiecePosition: Int
    ) {
        try {
            retailer.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    isProductTypeEmpty.postValue(productType.isEmpty())
                    isProductNameEmpty.postValue(productName.isEmpty())
                    isProductClothEmpty.postValue(productCloth.isEmpty())
                    isProductFabricEmpty.postValue(productFabric.isEmpty())
                    isProductPatternEmpty.postValue(productPattern.isEmpty())
                    isSetPieceEmpty.postValue(setPiece.isEmpty())
                    isTopMeasurementEmpty.postValue(topMeasurement.isEmpty())
                    isBottomMeasurementEmpty.postValue(if (setPiecePosition == 2) bottomMeasurement.isEmpty() else false)
                    isDupattaMeasurementEmpty.postValue(if (setPiecePosition != 0) dupattaMeasurement.isEmpty() else false)
                    isProductWeightEmpty.postValue(productWeight.isEmpty())
                    isDeliveryTimeEmpty.postValue(deliveryTime.isEmpty())
                    isQuantityEmpty.postValue(productQuantity.isEmpty())
                    isProductDescriptionEmpty.postValue(productDescription.isEmpty())
                    isProductColorEmpty.postValue(productColor.isEmpty())
                    isProductPriceEmpty.postValue(productPrice.isEmpty())
                    isProductImage1LengthZero.postValue(productImage1 == null)
                    isProductImage2LengthZero.postValue(productImage2 == null)

                    if (productType.isEmpty() ||
                        productName.isEmpty() ||
                        productCloth.isEmpty() ||
                        productFabric.isEmpty() ||
                        productPattern.isEmpty() ||
                        setPiece.isEmpty() ||
                        (if (setPiecePosition == 2) bottomMeasurement.isEmpty() else false) ||
                        (if (setPiecePosition != 0) dupattaMeasurement.isEmpty() else false) ||
                        productWeight.isEmpty() ||
                        deliveryTime.isEmpty() ||
                        topMeasurement.isEmpty() ||
                        productQuantity.isEmpty() ||
                        productColor.isEmpty() ||
                        productDescription.isEmpty() ||
                        productPrice.isEmpty() ||
                        productImage1 == null ||
                        productImage2 == null
                    )
                        return@launch

                    formDataMap.apply {
                        retailer.value?.let {
                            put("product_name", getFormRequestBody(productName))
                            put("product_category", getFormRequestBody(productCategory))
                            put("product_type", getFormRequestBody(productType))
                            put("product_description", getFormRequestBody(productDescription))
                            put("set_piece", getFormRequestBody(setPiece))
                            put("product_price", getFormRequestBody(productPrice))
                            put("product_colour", getFormRequestBody(productColor))
                            put("product_cloth", getFormRequestBody(productCloth))
                            put("product_fabric", getFormRequestBody(productFabric))
                            put("product_pattern", getFormRequestBody(productPattern))
                            put("weight", getFormRequestBody(productWeight))
                            put("top_measurement", getFormRequestBody(topMeasurement))
                            put("bottom_measurement", getFormRequestBody(if (setPiecePosition == 2) bottomMeasurement else ""))
                            put("dupatta_measurement", getFormRequestBody(if (setPiecePosition != 0) dupattaMeasurement else ""))
                            put("available_quantity", getFormRequestBody(productQuantity))
                            put("delivery_time", getFormRequestBody(deliveryTime))
                            put("business_category", getFormRequestBody(it.businessCategory))
                            put(
                                "business_category_type",
                                getFormRequestBody(it.businessCategoryType)
                            )
                            put("business_id", getFormRequestBody(it.shopId.toString()))
                            put("uuid", getFormRequestBody(it.uuid))
                            put("business_name", getFormRequestBody(it.businessName))
                            put("likes", getFormRequestBody("0"))
                            put("product_status", getFormRequestBody("0"))
                        }
                    }

                    addFileMultiPartBody("product_image1", productImage1)
                    addFileMultiPartBody("product_image2", productImage2)
                    if (productImage3 != null)
                        addFileMultiPartBody("product_image3", productImage3)
                    if (productImage4 != null)
                        addFileMultiPartBody("product_image4", productImage4)
                    if (productImage5 != null)
                        addFileMultiPartBody("product_image5", productImage5)
                    try {
                        val response =
                            boutiqueService.postStoreProduct(formDataMap, imageFiles).execute()
                        if (response.code() == 201) {
                            isStoreSubmissionSuccessful.postValue(true)
                            productImage1.delete()
                            productImage2.delete()
                            productImage3?.delete()
                            productImage4?.delete()
                            productImage5?.delete()
                        } else
                            isStoreSubmissionSuccessful.postValue(false)
                    } catch (e: Exception) {
                        isStoreSubmissionSuccessful.postValue(false)
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitJewellery(
        productType: String,
        productName: String,
        gender: String,
        productMaterial: String,
        productColor: String,
        productCategory: String = "Jewellery",
        deliveryTime: String,
        productQuantity: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        productPrice: String
    ) {
        try {
            retailer.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    isProductTypeEmpty.postValue(productType.isEmpty())
                    isProductNameEmpty.postValue(productName.isEmpty())
                    isDeliveryTimeEmpty.postValue(deliveryTime.isEmpty())
                    isProductMaterialEmpty.postValue(productMaterial.isEmpty())
                    isGenderEmpty.postValue(gender.isEmpty())
                    isQuantityEmpty.postValue(productQuantity.isEmpty())
                    isProductDescriptionEmpty.postValue(productDescription.isEmpty())
                    isProductColorEmpty.postValue(productColor.isEmpty())
                    isProductPriceEmpty.postValue(productPrice.isEmpty())
                    isProductImage1LengthZero.postValue(productImage1 == null)
                    isProductImage2LengthZero.postValue(productImage2 == null)

                    if (productType.isEmpty() ||
                        productName.isEmpty() ||
                        productMaterial.isEmpty() ||
                        deliveryTime.isEmpty() ||
                        gender.isEmpty() ||
                        productQuantity.isEmpty() ||
                        productColor.isEmpty() ||
                        productDescription.isEmpty() ||
                        productPrice.isEmpty() ||
                        productImage1 == null ||
                        productImage2 == null
                    )
                        return@launch

                    formDataMap.apply {
                        retailer.value?.let {
                            put("product_name", getFormRequestBody(productName))
                            put("product_category", getFormRequestBody(productCategory))
                            put("product_type", getFormRequestBody(productType))
                            put("product_description", getFormRequestBody(productDescription))
                            put("product_material", getFormRequestBody(productMaterial))
                            put("gender", getFormRequestBody(gender))
                            put("product_price", getFormRequestBody(productPrice))
                            put("product_colour", getFormRequestBody(productColor))
                            put("available_quantity", getFormRequestBody(productQuantity))
                            put("delivery_time", getFormRequestBody(deliveryTime))
                            put("business_category", getFormRequestBody(it.businessCategory))
                            put(
                                "business_category_type",
                                getFormRequestBody(it.businessCategoryType)
                            )
                            put("business_id", getFormRequestBody(it.shopId.toString()))
                            put("uuid", getFormRequestBody(it.uuid))
                            put("business_name", getFormRequestBody(it.businessName))
                            put("likes", getFormRequestBody("0"))
                            put("product_status", getFormRequestBody("0"))
                        }
                    }

                    addFileMultiPartBody("product_image1", productImage1)
                    addFileMultiPartBody("product_image2", productImage2)
                    if (productImage3 != null)
                        addFileMultiPartBody("product_image3", productImage3)
                    if (productImage4 != null)
                        addFileMultiPartBody("product_image4", productImage4)
                    if (productImage5 != null)
                        addFileMultiPartBody("product_image5", productImage5)
                    try {
                        val response =
                            boutiqueService.postStoreProduct(formDataMap, imageFiles).execute()
                        if (response.code() == 201) {
                            isStoreSubmissionSuccessful.postValue(true)
                            productImage1.delete()
                            productImage2.delete()
                            productImage3?.delete()
                            productImage4?.delete()
                            productImage5?.delete()
                        } else
                            isStoreSubmissionSuccessful.postValue(false)
                    } catch (e: Exception) {
                        isStoreSubmissionSuccessful.postValue(false)
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitCloth(
        productType: String,
        productName: String,
        productCloth: String,
        gender: String,
        productOccasion: String,
        productFabric: String,
        isSAvailable: Boolean,
        isMAvailable: Boolean,
        isLAvailable: Boolean,
        isXLAvailable: Boolean,
        isXXLAvailable: Boolean,
        productCategory: String = "Clothing",
        deliveryTime: String,
        productQuantity: String,
        productColor: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        productPrice: String
    ) {
        try {
            retailer.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    isProductTypeEmpty.postValue(productType.isEmpty())
                    isProductNameEmpty.postValue(productName.isEmpty())
                    isProductClothEmpty.postValue(productCloth.isEmpty())
                    isProductFabricEmpty.postValue(productFabric.isEmpty())
                    isDeliveryTimeEmpty.postValue(deliveryTime.isEmpty())
                    isProductOccasionEmpty.postValue(productOccasion.isEmpty())
                    isGenderEmpty.postValue(gender.isEmpty())
                    noSizesAvailable.postValue(!isSAvailable && !isMAvailable && !isLAvailable && !isXLAvailable && !isXXLAvailable)
                    isQuantityEmpty.postValue(productQuantity.isEmpty())
                    isProductDescriptionEmpty.postValue(productDescription.isEmpty())
                    isProductColorEmpty.postValue(productColor.isEmpty())
                    isProductPriceEmpty.postValue(productPrice.isEmpty())
                    isProductImage1LengthZero.postValue(productImage1 == null)
                    isProductImage2LengthZero.postValue(productImage2 == null)

                    if (productType.isEmpty() ||
                        productName.isEmpty() ||
                        productCloth.isEmpty() ||
                        productFabric.isEmpty() ||
                        gender.isEmpty() ||
                        productOccasion.isEmpty() ||
                        deliveryTime.isEmpty() ||
                        productQuantity.isEmpty() ||
                        productColor.isEmpty() ||
                        productDescription.isEmpty() ||
                        productPrice.isEmpty() ||
                        productImage1 == null ||
                        productImage2 == null ||
                        (!isSAvailable && !isMAvailable && !isLAvailable && !isXLAvailable && !isXXLAvailable)
                    )
                        return@launch

                    formDataMap.apply {
                        retailer.value?.let {
                            put("product_name", getFormRequestBody(productName))
                            put("product_category", getFormRequestBody(productCategory))
                            put("product_type", getFormRequestBody(productType))
                            put("product_description", getFormRequestBody(productDescription))
                            put("product_occasion", getFormRequestBody(productOccasion))
                            put("gender", getFormRequestBody(gender))
                            put("size_s", getFormRequestBody(isSAvailable.toInt().toString()))
                            put("size_m", getFormRequestBody(isMAvailable.toInt().toString()))
                            put("size_l", getFormRequestBody(isLAvailable.toInt().toString()))
                            put("size_xl", getFormRequestBody(isXLAvailable.toInt().toString()))
                            put("size_xxl", getFormRequestBody(isXXLAvailable.toInt().toString()))
                            put("product_price", getFormRequestBody(productPrice))
                            put("product_colour", getFormRequestBody(productColor))
                            put("product_cloth", getFormRequestBody(productCloth))
                            put("product_fabric", getFormRequestBody(productFabric))
                            put("available_quantity", getFormRequestBody(productQuantity))
                            put("delivery_time", getFormRequestBody(deliveryTime))
                            put("business_category", getFormRequestBody(it.businessCategory))
                            put(
                                "business_category_type",
                                getFormRequestBody(it.businessCategoryType)
                            )
                            put("business_id", getFormRequestBody(it.shopId.toString()))
                            put("uuid", getFormRequestBody(it.uuid))
                            put("business_name", getFormRequestBody(it.businessName))
                            put("likes", getFormRequestBody("0"))
                            put("product_status", getFormRequestBody("0"))
                        }
                    }

                    addFileMultiPartBody("product_image1", productImage1)
                    addFileMultiPartBody("product_image2", productImage2)
                    if (productImage3 != null)
                        addFileMultiPartBody("product_image3", productImage3)
                    if (productImage4 != null)
                        addFileMultiPartBody("product_image4", productImage4)
                    if (productImage5 != null)
                        addFileMultiPartBody("product_image5", productImage5)
                    try {
                        val response =
                            boutiqueService.postStoreProduct(formDataMap, imageFiles).execute()
                        if (response.code() == 201) {
                            isStoreSubmissionSuccessful.postValue(true)
                            productImage1.delete()
                            productImage2.delete()
                            productImage3?.delete()
                            productImage4?.delete()
                            productImage5?.delete()
                        } else
                            isStoreSubmissionSuccessful.postValue(false)
                    } catch (e: Exception) {
                        isStoreSubmissionSuccessful.postValue(false)
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isProductDataValid(
        productType: String,
        productName: String,
        productCloth: String,
        productFabric: String,
        productOccasion: String,
        preparationTime: String,
        productColor: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        startPrice: Int,
        endPrice: Int,
        businessId: String,
        businessName: String,
        uuid: String,
        zone: String
    ): Boolean {
        val tempDataMap = HashMap<String, RequestBody>()

        isProductNameEmpty.value = productName.isEmpty()
        if (isProductNameEmpty.value!!)
            return false
        tempDataMap["product_name"] = getFormRequestBody(productName)

        isProductTypeEmpty.value = productType.isEmpty()
        if (isProductTypeEmpty.value!!)
            return false
        tempDataMap["product_type"] = getFormRequestBody(productType)

        isProductClothEmpty.value = productCloth.isEmpty()
        if (isProductClothEmpty.value!!)
            return false
        tempDataMap["product_cloth"] = getFormRequestBody(productCloth)

        isProductFabricEmpty.value = productFabric.isEmpty()
        if (isProductFabricEmpty.value!!)
            return false
        tempDataMap["product_fabric"] = getFormRequestBody(productFabric)

        isProductOccasionEmpty.value = productOccasion.isEmpty()
        if (isProductOccasionEmpty.value!!)
            return false
        tempDataMap["product_occasion"] = getFormRequestBody(productOccasion)

        isPreparationTimeEmpty.value = preparationTime.isEmpty()
        if (isPreparationTimeEmpty.value!!)
            return false
        tempDataMap["preparation_time"] = getFormRequestBody(preparationTime)

        isProductColorEmpty.value = productColor.isEmpty()
        if (isProductColorEmpty.value!!)
            return false
        tempDataMap["product_colour"] = getFormRequestBody(productColor)

        isProductDescriptionEmpty.value = productDescription.isEmpty()
        if (isProductDescriptionEmpty.value!!)
            return false
        tempDataMap["product_description"] = getFormRequestBody(productDescription)

        isProductImage1LengthZero.value = productImage1 == null || productImage1.length() == 0L
        if (isProductImage1LengthZero.value!!)
            return false

        isProductImage2LengthZero.value = productImage2 == null || productImage2.length() == 0L
        if (isProductImage2LengthZero.value!!)
            return false
        addFileMultiPartBody("product_image1", productImage1)
        addFileMultiPartBody("product_image2", productImage2)
        addFileMultiPartBody("product_image3", productImage3)
        addFileMultiPartBody("product_image4", productImage4)
        addFileMultiPartBody("product_image5", productImage5)

        isStartPriceZero.value = startPrice == 0
        if (isStartPriceZero.value!!)
            return false
        tempDataMap["start_price"] = getFormRequestBody(startPrice.toString())
        tempDataMap["end_price"] = getFormRequestBody(endPrice.toString())

        isBusinessIdEmpty.value = businessId.isEmpty()
        if (isBusinessIdEmpty.value!!)
            return false
        tempDataMap["business_id"] = getFormRequestBody(businessId)

        isBusinessNameEmpty.value = businessName.isEmpty()
        if (isBusinessNameEmpty.value!!)
            return false
        tempDataMap["business_name"] = getFormRequestBody(businessName)

        isUuidEmpty.value = uuid.isEmpty()
        if (isUuidEmpty.value!!)
            return false
        tempDataMap["uuid"] = getFormRequestBody(uuid)

        isZoneEmpty.value = zone.isEmpty()
        if (isZoneEmpty.value!!)
            return false
        tempDataMap["zone"] = getFormRequestBody(zone)

        formDataMap.putAll(tempDataMap)

        // Clear temporary maps
        tempDataMap.clear()
        return true
    }

    private fun isFabricDataValid(
        productType: String,
        productName: String,
        productCloth: String,
        productFabric: String,
        deliveryTime: String,
        productQuantity: String,
        productColor: String,
        productDescription: String,
        productImage1: File?,
        productImage2: File?,
        productImage3: File?,
        productImage4: File?,
        productImage5: File?,
        productPrice: String,
        businessId: String,
        businessName: String,
        uuid: String
    ): Boolean {
        val tempDataMap = HashMap<String, RequestBody>()

        isProductNameEmpty.value = productName.isEmpty()
        if (isProductNameEmpty.value!!)
            return false
        tempDataMap["product_name"] = getFormRequestBody(productName)

        isProductTypeEmpty.value = productType.isEmpty()
        if (isProductTypeEmpty.value!!)
            return false
        tempDataMap["product_type"] = getFormRequestBody(productType)

        isProductClothEmpty.value = productCloth.isEmpty()
        if (isProductClothEmpty.value!!)
            return false
        tempDataMap["product_cloth"] = getFormRequestBody(productCloth)

        isProductFabricEmpty.value = productFabric.isEmpty()
        if (isProductFabricEmpty.value!!)
            return false
        tempDataMap["product_fabric"] = getFormRequestBody(productFabric)

        isDeliveryTimeEmpty.value = deliveryTime.isEmpty()
        if (isDeliveryTimeEmpty.value!!)
            return false
        tempDataMap["delivery_time"] = getFormRequestBody(deliveryTime)

        isProductColorEmpty.value = productColor.isEmpty()
        if (isProductColorEmpty.value!!)
            return false
        tempDataMap["product_colour"] = getFormRequestBody(productColor)

        isProductDescriptionEmpty.value = productDescription.isEmpty()
        if (isProductDescriptionEmpty.value!!)
            return false
        tempDataMap["product_description"] = getFormRequestBody(productDescription)

        isProductImage1LengthZero.value = productImage1 == null || productImage1.length() == 0L
        if (isProductImage1LengthZero.value!!)
            return false

        isProductImage2LengthZero.value = productImage2 == null || productImage2.length() == 0L
        if (isProductImage2LengthZero.value!!)
            return false
        addFileMultiPartBody("product_image1", productImage1)
        addFileMultiPartBody("product_image2", productImage2)
        addFileMultiPartBody("product_image3", productImage3)
        addFileMultiPartBody("product_image4", productImage4)
        addFileMultiPartBody("product_image5", productImage5)

        isProductPriceEmpty.value = productPrice.isEmpty()
        if (isProductPriceEmpty.value!!)
            return false
        tempDataMap["product_price"] = getFormRequestBody(productPrice)

        isQuantityEmpty.value = productQuantity.isEmpty()
        if (isQuantityEmpty.value!!)
            return false
        tempDataMap["available_meters"] = getFormRequestBody(productQuantity)

        isBusinessIdEmpty.value = businessId.isEmpty()
        if (isBusinessIdEmpty.value!!)
            return false
        tempDataMap["business_id"] = getFormRequestBody(businessId)

        isBusinessNameEmpty.value = businessName.isEmpty()
        if (isBusinessNameEmpty.value!!)
            return false
        tempDataMap["business_name"] = getFormRequestBody(businessName)

        isUuidEmpty.value = uuid.isEmpty()
        if (isUuidEmpty.value!!)
            return false
        tempDataMap["uuid"] = getFormRequestBody(uuid)

        formDataMap.putAll(tempDataMap)

        // Clear temporary maps
        tempDataMap.clear()
        return true
    }

    private fun addFileMultiPartBody(partName: String, file: File?) {
        file?.let {
            val requestBody = RequestBody.create(MediaType.parse("image/*"), it)
            imageFiles.add(MultipartBody.Part.createFormData(partName, it.name, requestBody))
        }
    }

    private fun getFormRequestBody(field: String): RequestBody =
        RequestBody.create(MediaType.parse("text/plain"), field)

    private fun Boolean.toInt(): Int =
        if (this)
            1
        else
            0
}