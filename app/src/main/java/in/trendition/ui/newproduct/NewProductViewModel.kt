package `in`.trendition.ui.newproduct

import `in`.trendition.model.Palette
import `in`.trendition.model.Retailer
import `in`.trendition.network.BoutiqueService
import `in`.trendition.repository.ProfileRepository
import `in`.trendition.util.Constants
import `in`.trendition.util.ProgressUpload
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
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
    private val isEndPriceZero: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isBusinessIdEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isBusinessNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isUuidEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isZoneEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isDeliveryTimeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isQuantityEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductPriceEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductStoryEmpty: MutableLiveData<Boolean> = MutableLiveData(false)

    // Dropdown items.
    private val designDropDownItems: MutableLiveData<Pair<Boolean, List<String>>> =
        MutableLiveData(Pair(first = false, second = ArrayList()))
    private val clothDropDownItems: MutableLiveData<Pair<Boolean, List<String>>> =
        MutableLiveData(Pair(first = false, second = ArrayList()))
    private val clothingTypeDropDownItems: MutableLiveData<Pair<Boolean, Map<String, List<String>>>> =
        MutableLiveData(Pair(false, HashMap<String, List<String>>().apply {
            put("Male", emptyList())
            put("Female", emptyList())
            put("Unisex", emptyList())
        }))
    private val jewelleryTypeDropDownItems: MutableLiveData<Pair<Boolean, Map<String, List<String>>>> =
        MutableLiveData(Pair(false, HashMap<String, List<String>>().apply {
            put("Male", emptyList())
            put("Female", emptyList())
            put("Unisex", emptyList())
        }))

    // Know if the product submission is successful.
    private val isProductSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    private val isStoreSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    private val isSketchSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    private val contentMap = HashMap<String, ProgressUpload.ProgressRequestBody>()

    // List of MultipartBody.Part for image files
    private val imageFiles = ArrayList<MultipartBody.Part>()

    private val progressUpload = ProgressUpload()

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
    fun getIsEndPriceZero(): LiveData<Boolean> = isEndPriceZero
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

    // Dropdown getters.
    fun getDesignDropDownItems(): LiveData<Pair<Boolean, List<String>>> = designDropDownItems
    fun getClothDropDownItems(): LiveData<Pair<Boolean, List<String>>> = clothDropDownItems
    fun getClothingTypeDropDownItems(): LiveData<Pair<Boolean, Map<String, List<String>>>> =
        clothingTypeDropDownItems

    fun getJewelleryTypeDropDownItems(): LiveData<Pair<Boolean, Map<String, List<String>>>> =
        jewelleryTypeDropDownItems

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
        startPrice: String,
        endPrice: String,
        listener: ProgressUpload.UploadListener
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
                            progressUpload.apply {
                                prepareToUpload(contentMap = contentMap, files = imageFiles)
                                setUploadListener(listener = listener)
                            }
                            val response =
                                boutiqueService.postProduct(contentMap, imageFiles).execute()
                            if (response.isSuccessful && response.code() == 201) {
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
        productImage5: File?,
        listener: ProgressUpload.UploadListener
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            isProductNameEmpty.postValue(productName.isEmpty())
            isProductStoryEmpty.postValue(productStory.isEmpty())
            isProductDescriptionEmpty.postValue(productDescription.isEmpty())
            isProductImage1LengthZero.postValue(productImage1 == null)
            isProductImage2LengthZero.postValue(productImage2 == null)

            if (productName.isEmpty() || productStory.isEmpty() || productDescription.isEmpty() || productImage1 == null || productImage2 == null)
                return@launch

            contentMap.apply {
                put("product_name", getFormProgressRequestBody(progressUpload, productName))
                put(
                    "product_description",
                    getFormProgressRequestBody(progressUpload, productDescription)
                )
                put("product_story", getFormProgressRequestBody(progressUpload, productStory))
                retailer.value?.let {
                    put(
                        "business_id",
                        getFormProgressRequestBody(progressUpload, it.shopId.toString())
                    )
                    put("uuid", getFormProgressRequestBody(progressUpload, it.uuid))
                    put(
                        "business_name",
                        getFormProgressRequestBody(progressUpload, it.businessName)
                    )
                }
                put("product_status", getFormProgressRequestBody(progressUpload, "0"))
            }

            addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
            addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
            addMultiPartProgressRequestBody(progressUpload, "product_image3", productImage3)
            addMultiPartProgressRequestBody(progressUpload, "product_image4", productImage4)
            addMultiPartProgressRequestBody(progressUpload, "product_image5", productImage5)

            try {
                progressUpload.apply {
                    prepareToUpload(contentMap = contentMap, files = imageFiles)
                    setUploadListener(listener = listener)
                }
                val response = boutiqueService.postSketch(contentMap, imageFiles).execute()
                if (response.code() == 201)
                    isSketchSubmissionSuccessful.postValue(true)
                else
                    isSketchSubmissionSuccessful.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                isSketchSubmissionSuccessful.postValue(false)
            }
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
        productPrice: String,
        listener: ProgressUpload.UploadListener
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

                    contentMap.apply {
                        retailer.value?.let {
                            put(
                                "product_name", getFormProgressRequestBody(
                                    progressUpload,
                                    productName
                                )
                            )
                            put(
                                "product_category", getFormProgressRequestBody(
                                    progressUpload,
                                    productCategory
                                )
                            )
                            put(
                                "product_type", getFormProgressRequestBody(
                                    progressUpload,
                                    productType
                                )
                            )
                            put(
                                "product_description", getFormProgressRequestBody(
                                    progressUpload,
                                    productDescription
                                )
                            )
                            put(
                                "product_price", getFormProgressRequestBody(
                                    progressUpload,
                                    productPrice
                                )
                            )
                            put(
                                "product_colour", getFormProgressRequestBody(
                                    progressUpload,
                                    productColor
                                )
                            )
                            put(
                                "product_cloth", getFormProgressRequestBody(
                                    progressUpload,
                                    productCloth
                                )
                            )
                            put(
                                "product_fabric", getFormProgressRequestBody(
                                    progressUpload,
                                    productFabric
                                )
                            )
                            put(
                                "product_pattern", getFormProgressRequestBody(
                                    progressUpload,
                                    productPattern
                                )
                            )
                            put("width", getFormProgressRequestBody(progressUpload, productWidth))
                            put("weight", getFormProgressRequestBody(progressUpload, productWeight))
                            put(
                                "available_quantity", getFormProgressRequestBody(
                                    progressUpload,
                                    productQuantity
                                )
                            )
                            put(
                                "minimum_quantity", getFormProgressRequestBody(
                                    progressUpload,
                                    minimumQuantity
                                )
                            )
                            put(
                                "delivery_time", getFormProgressRequestBody(
                                    progressUpload,
                                    deliveryTime
                                )
                            )
                            put(
                                "business_category", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessCategory
                                )
                            )
                            put(
                                "business_category_type",
                                getFormProgressRequestBody(progressUpload, it.specialization)
                            )
                            put(
                                "business_id", getFormProgressRequestBody(
                                    progressUpload,
                                    it.shopId.toString()
                                )
                            )
                            put("uuid", getFormProgressRequestBody(progressUpload, it.uuid))
                            put(
                                "business_name", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessName
                                )
                            )
                            put("likes", getFormProgressRequestBody(progressUpload, "0"))
                            put("product_status", getFormProgressRequestBody(progressUpload, "0"))
                        }
                    }

                    addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
                    addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
                    if (productImage3 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image3",
                            productImage3
                        )
                    if (productImage4 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image4",
                            productImage4
                        )
                    if (productImage5 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image5",
                            productImage5
                        )
                    try {
                        progressUpload.apply {
                            prepareToUpload(contentMap = contentMap, files = imageFiles)
                            setUploadListener(listener = listener)
                        }
                        val response =
                            boutiqueService.postStoreProduct(contentMap, imageFiles).execute()
                        if (response.code() == 201) {
                            isStoreSubmissionSuccessful.postValue(true)
                            productImage1.delete()
                            productImage2.delete()
                            productImage3?.delete()
                            productImage4?.delete()
                            productImage5?.delete()
                        } else {
                            response.errorBody()?.let {
                                Log.d(
                                    this@NewProductViewModel.javaClass.simpleName,
                                    it.string()
                                )
                            }
                            isStoreSubmissionSuccessful.postValue(false)
                        }
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
        gender: String,
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
        setPiecePosition: Int,
        listener: ProgressUpload.UploadListener
    ) {
        try {
            retailer.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    isProductTypeEmpty.postValue(productType.isEmpty())
                    isProductNameEmpty.postValue(productName.isEmpty())
                    isProductClothEmpty.postValue(productCloth.isEmpty())
                    isProductFabricEmpty.postValue(productFabric.isEmpty())
                    isProductPatternEmpty.postValue(productPattern.isEmpty())
                    isGenderEmpty.postValue(gender.isEmpty())
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

                    contentMap.apply {
                        retailer.value?.let {
                            put(
                                "product_name", getFormProgressRequestBody(
                                    progressUpload,
                                    productName
                                )
                            )
                            put(
                                "product_category", getFormProgressRequestBody(
                                    progressUpload,
                                    productCategory
                                )
                            )
                            put(
                                "product_type", getFormProgressRequestBody(
                                    progressUpload,
                                    productType
                                )
                            )
                            put(
                                "product_description", getFormProgressRequestBody(
                                    progressUpload,
                                    productDescription
                                )
                            )
                            put("set_piece", getFormProgressRequestBody(progressUpload, setPiece))
                            put(
                                "product_price", getFormProgressRequestBody(
                                    progressUpload,
                                    productPrice
                                )
                            )
                            put("gender", getFormProgressRequestBody(progressUpload, gender))
                            put(
                                "product_colour", getFormProgressRequestBody(
                                    progressUpload,
                                    productColor
                                )
                            )
                            put(
                                "product_cloth", getFormProgressRequestBody(
                                    progressUpload,
                                    productCloth
                                )
                            )
                            put(
                                "product_fabric", getFormProgressRequestBody(
                                    progressUpload,
                                    productFabric
                                )
                            )
                            put(
                                "product_pattern", getFormProgressRequestBody(
                                    progressUpload,
                                    productPattern
                                )
                            )
                            put("weight", getFormProgressRequestBody(progressUpload, productWeight))
                            put(
                                "top_measurement", getFormProgressRequestBody(
                                    progressUpload,
                                    topMeasurement
                                )
                            )
                            put(
                                "bottom_measurement",
                                getFormProgressRequestBody(
                                    progressUpload,
                                    if (setPiecePosition == 2) bottomMeasurement else ""
                                )
                            )
                            put(
                                "dupatta_measurement",
                                getFormProgressRequestBody(
                                    progressUpload,
                                    if (setPiecePosition != 0) dupattaMeasurement else ""
                                )
                            )
                            put(
                                "available_quantity", getFormProgressRequestBody(
                                    progressUpload,
                                    productQuantity
                                )
                            )
                            put(
                                "delivery_time", getFormProgressRequestBody(
                                    progressUpload,
                                    deliveryTime
                                )
                            )
                            put(
                                "business_category", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessCategory
                                )
                            )
                            put(
                                "business_category_type",
                                getFormProgressRequestBody(progressUpload, it.specialization)
                            )
                            put(
                                "business_id", getFormProgressRequestBody(
                                    progressUpload,
                                    it.shopId.toString()
                                )
                            )
                            put("uuid", getFormProgressRequestBody(progressUpload, it.uuid))
                            put(
                                "business_name", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessName
                                )
                            )
                            put("likes", getFormProgressRequestBody(progressUpload, "0"))
                            put("product_status", getFormProgressRequestBody(progressUpload, "0"))
                        }
                    }

                    addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
                    addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
                    if (productImage3 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image3",
                            productImage3
                        )
                    if (productImage4 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image4",
                            productImage4
                        )
                    if (productImage5 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image5",
                            productImage5
                        )
                    try {
                        progressUpload.apply {
                            prepareToUpload(contentMap = contentMap, files = imageFiles)
                            setUploadListener(listener = listener)
                        }
                        val response =
                            boutiqueService.postStoreProduct(contentMap, imageFiles).execute()
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
        productPrice: String,
        listener: ProgressUpload.UploadListener
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

                    contentMap.apply {
                        retailer.value?.let {
                            put(
                                "product_name", getFormProgressRequestBody(
                                    progressUpload,
                                    productName
                                )
                            )
                            put(
                                "product_category", getFormProgressRequestBody(
                                    progressUpload,
                                    productCategory
                                )
                            )
                            put(
                                "product_type", getFormProgressRequestBody(
                                    progressUpload,
                                    productType
                                )
                            )
                            put(
                                "product_description", getFormProgressRequestBody(
                                    progressUpload,
                                    productDescription
                                )
                            )
                            put(
                                "product_material", getFormProgressRequestBody(
                                    progressUpload,
                                    productMaterial
                                )
                            )
                            put("gender", getFormProgressRequestBody(progressUpload, gender))
                            put(
                                "product_price", getFormProgressRequestBody(
                                    progressUpload,
                                    productPrice
                                )
                            )
                            put(
                                "product_colour", getFormProgressRequestBody(
                                    progressUpload,
                                    productColor
                                )
                            )
                            put(
                                "available_quantity", getFormProgressRequestBody(
                                    progressUpload,
                                    productQuantity
                                )
                            )
                            put(
                                "delivery_time", getFormProgressRequestBody(
                                    progressUpload,
                                    deliveryTime
                                )
                            )
                            put(
                                "business_category", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessCategory
                                )
                            )
                            put(
                                "business_category_type",
                                getFormProgressRequestBody(progressUpload, it.specialization)
                            )
                            put(
                                "business_id", getFormProgressRequestBody(
                                    progressUpload,
                                    it.shopId.toString()
                                )
                            )
                            put("uuid", getFormProgressRequestBody(progressUpload, it.uuid))
                            put(
                                "business_name", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessName
                                )
                            )
                            put("likes", getFormProgressRequestBody(progressUpload, "0"))
                            put("product_status", getFormProgressRequestBody(progressUpload, "0"))
                        }
                    }

                    addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
                    addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
                    if (productImage3 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image3",
                            productImage3
                        )
                    if (productImage4 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image4",
                            productImage4
                        )
                    if (productImage5 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image5",
                            productImage5
                        )
                    try {
                        progressUpload.apply {
                            prepareToUpload(contentMap = contentMap, files = imageFiles)
                            setUploadListener(listener = listener)
                        }
                        val response =
                            boutiqueService.postStoreProduct(contentMap, imageFiles).execute()
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
        productPrice: String,
        listener: ProgressUpload.UploadListener
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
                    noSizesAvailable.postValue(!(productType == "Dupatta" || productType == "Sarees" || productType == "Shawl/Stoles") && !isSAvailable && !isMAvailable && !isLAvailable && !isXLAvailable && !isXXLAvailable)
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
                        (!(productType == "Dupatta" || productType == "Sarees" || productType == "Shawl/Stoles") && !isSAvailable && !isMAvailable && !isLAvailable && !isXLAvailable && !isXXLAvailable)
                    )
                        return@launch

                    contentMap.apply {
                        retailer.value?.let {
                            put(
                                "product_name", getFormProgressRequestBody(
                                    progressUpload,
                                    productName
                                )
                            )
                            put(
                                "product_category", getFormProgressRequestBody(
                                    progressUpload,
                                    productCategory
                                )
                            )
                            put(
                                "product_type", getFormProgressRequestBody(
                                    progressUpload,
                                    productType
                                )
                            )
                            put(
                                "product_description", getFormProgressRequestBody(
                                    progressUpload,
                                    productDescription
                                )
                            )
                            put(
                                "product_occasion", getFormProgressRequestBody(
                                    progressUpload,
                                    productOccasion
                                )
                            )
                            put("gender", getFormProgressRequestBody(progressUpload, gender))
                            put(
                                "size_s", getFormProgressRequestBody(
                                    progressUpload,
                                    isSAvailable.toInt().toString()
                                )
                            )
                            put(
                                "size_m", getFormProgressRequestBody(
                                    progressUpload,
                                    isMAvailable.toInt().toString()
                                )
                            )
                            put(
                                "size_l", getFormProgressRequestBody(
                                    progressUpload,
                                    isLAvailable.toInt().toString()
                                )
                            )
                            put(
                                "size_xl", getFormProgressRequestBody(
                                    progressUpload,
                                    isXLAvailable.toInt().toString()
                                )
                            )
                            put(
                                "size_xxl", getFormProgressRequestBody(
                                    progressUpload,
                                    isXXLAvailable.toInt().toString()
                                )
                            )
                            put(
                                "product_price", getFormProgressRequestBody(
                                    progressUpload,
                                    productPrice
                                )
                            )
                            put(
                                "product_colour", getFormProgressRequestBody(
                                    progressUpload,
                                    productColor
                                )
                            )
                            put(
                                "product_cloth", getFormProgressRequestBody(
                                    progressUpload,
                                    productCloth
                                )
                            )
                            put(
                                "product_fabric", getFormProgressRequestBody(
                                    progressUpload,
                                    productFabric
                                )
                            )
                            put(
                                "available_quantity", getFormProgressRequestBody(
                                    progressUpload,
                                    productQuantity
                                )
                            )
                            put(
                                "delivery_time", getFormProgressRequestBody(
                                    progressUpload,
                                    deliveryTime
                                )
                            )
                            put(
                                "business_category", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessCategory
                                )
                            )
                            put(
                                "business_category_type",
                                getFormProgressRequestBody(progressUpload, it.specialization)
                            )
                            put(
                                "business_id", getFormProgressRequestBody(
                                    progressUpload,
                                    it.shopId.toString()
                                )
                            )
                            put("uuid", getFormProgressRequestBody(progressUpload, it.uuid))
                            put(
                                "business_name", getFormProgressRequestBody(
                                    progressUpload,
                                    it.businessName
                                )
                            )
                            put("likes", getFormProgressRequestBody(progressUpload, "0"))
                            put("product_status", getFormProgressRequestBody(progressUpload, "0"))
                        }
                    }

                    addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
                    addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
                    if (productImage3 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image3",
                            productImage3
                        )
                    if (productImage4 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image4",
                            productImage4
                        )
                    if (productImage5 != null)
                        addMultiPartProgressRequestBody(
                            progressUpload,
                            "product_image5",
                            productImage5
                        )
                    try {
                        progressUpload.apply {
                            prepareToUpload(contentMap = contentMap, files = imageFiles)
                            setUploadListener(listener = listener)
                        }
                        val response =
                            boutiqueService.postStoreProduct(contentMap, imageFiles).execute()
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
        startPrice: String,
        endPrice: String,
        businessId: String,
        businessName: String,
        uuid: String,
        zone: String
    ): Boolean {
        val tempDataMap = HashMap<String, ProgressUpload.ProgressRequestBody>()

        isProductNameEmpty.value = productName.isEmpty()
        if (isProductNameEmpty.value!!)
            return false
        tempDataMap["product_name"] = getFormProgressRequestBody(progressUpload, productName)

        isProductTypeEmpty.value = productType.isEmpty()
        if (isProductTypeEmpty.value!!)
            return false
        tempDataMap["product_type"] = getFormProgressRequestBody(progressUpload, productType)

        isProductClothEmpty.value = productCloth.isEmpty()
        if (isProductClothEmpty.value!!)
            return false
        tempDataMap["product_cloth"] = getFormProgressRequestBody(progressUpload, productCloth)

        isProductFabricEmpty.value = productFabric.isEmpty()
        if (isProductFabricEmpty.value!!)
            return false
        tempDataMap["product_fabric"] = getFormProgressRequestBody(progressUpload, productFabric)

        isProductOccasionEmpty.value = productOccasion.isEmpty()
        if (isProductOccasionEmpty.value!!)
            return false
        tempDataMap["product_occasion"] = getFormProgressRequestBody(
            progressUpload,
            productOccasion
        )

        isPreparationTimeEmpty.value = preparationTime.isEmpty()
        if (isPreparationTimeEmpty.value!!)
            return false
        tempDataMap["preparation_time"] = getFormProgressRequestBody(
            progressUpload,
            preparationTime
        )

        isProductColorEmpty.value = productColor.isEmpty()
        if (isProductColorEmpty.value!!)
            return false
        tempDataMap["product_colour"] = getFormProgressRequestBody(progressUpload, productColor)

        isProductDescriptionEmpty.value = productDescription.isEmpty()
        if (isProductDescriptionEmpty.value!!)
            return false
        tempDataMap["product_description"] = getFormProgressRequestBody(
            progressUpload,
            productDescription
        )

        isProductImage1LengthZero.value = productImage1 == null || productImage1.length() == 0L
        if (isProductImage1LengthZero.value!!)
            return false

        isProductImage2LengthZero.value = productImage2 == null || productImage2.length() == 0L
        if (isProductImage2LengthZero.value!!)
            return false
        addMultiPartProgressRequestBody(progressUpload, "product_image1", productImage1)
        addMultiPartProgressRequestBody(progressUpload, "product_image2", productImage2)
        addMultiPartProgressRequestBody(progressUpload, "product_image3", productImage3)
        addMultiPartProgressRequestBody(progressUpload, "product_image4", productImage4)
        addMultiPartProgressRequestBody(progressUpload, "product_image5", productImage5)

        isStartPriceZero.value = startPrice.isEmpty()
        if (isStartPriceZero.value!!)
            return false
        tempDataMap["start_price"] = getFormProgressRequestBody(progressUpload, startPrice)

        isEndPriceZero.value = endPrice.isEmpty()
        if (isEndPriceZero.value!!)
            return false
        tempDataMap["end_price"] = getFormProgressRequestBody(progressUpload, endPrice)

        isBusinessIdEmpty.value = businessId.isEmpty()
        if (isBusinessIdEmpty.value!!)
            return false
        tempDataMap["business_id"] = getFormProgressRequestBody(progressUpload, businessId)

        isBusinessNameEmpty.value = businessName.isEmpty()
        if (isBusinessNameEmpty.value!!)
            return false
        tempDataMap["business_name"] = getFormProgressRequestBody(progressUpload, businessName)

        isUuidEmpty.value = uuid.isEmpty()
        if (isUuidEmpty.value!!)
            return false
        tempDataMap["uuid"] = getFormProgressRequestBody(progressUpload, uuid)

        isZoneEmpty.value = zone.isEmpty()
        if (isZoneEmpty.value!!)
            return false
        tempDataMap["zone"] = getFormProgressRequestBody(progressUpload, zone)

        contentMap.putAll(tempDataMap)

        // Clear temporary maps
        tempDataMap.clear()
        return true
    }

    // Fetch Design Dropdown items.
    private suspend fun fetchDesignDropDownItems() = withContext(Dispatchers.IO) {
        val response = boutiqueService.getDesignDropDownItems().execute()
        if (response.isSuccessful)
            response.body()?.let { designDropDownItems ->
                this@NewProductViewModel.designDropDownItems.postValue(
                    Pair(
                        first = true,
                        second = designDropDownItems
                    )
                )
            } ?: emptyList<String>()
        else
            designDropDownItems.postValue(Pair(first = true, second = emptyList()))
    }

    // Fetch Cloth Dropdown items.
    private suspend fun fetchClothDropDownItems() = withContext(Dispatchers.IO) {
        val response = boutiqueService.getClothDropDownItems().execute()
        if (response.isSuccessful)
            response.body()?.let { clothDropDownItems ->
                this@NewProductViewModel.clothDropDownItems.postValue(
                    Pair(
                        first = true,
                        second = clothDropDownItems
                    )
                )
            } ?: emptyList<String>()
        else
            clothDropDownItems.postValue(Pair(first = true, second = emptyList()))
    }

    // Fetch Clothing Type Dropdown items.
    private suspend fun fetchClothingTypeDropDownItems() = withContext(Dispatchers.IO) {
        val response = boutiqueService.getClothingTypeDropDownItems().execute()
        if (response.isSuccessful)
            response.body()?.let { clothingTypeDropDownItems ->
                this@NewProductViewModel.clothingTypeDropDownItems.postValue(
                    Pair(
                        first = true,
                        second = clothingTypeDropDownItems
                    )
                )
            } ?: emptyList<String>()
        else
            clothingTypeDropDownItems.postValue(
                Pair(
                    first = true,
                    second = clothingTypeDropDownItems.value!!.second
                )
            )
    }

    // Fetch Jewellery Type Dropdown items.
    private suspend fun fetchJewelleryTypeDropDownItems() = withContext(Dispatchers.IO) {
        val response = boutiqueService.getJewelleryTypeDropDownItems().execute()
        if (response.isSuccessful)
            response.body()?.let { jewelleryTypeDropDownItems ->
                this@NewProductViewModel.jewelleryTypeDropDownItems.postValue(
                    Pair(
                        first = true,
                        second = jewelleryTypeDropDownItems
                    )
                )
            } ?: emptyList<String>()
        else
            jewelleryTypeDropDownItems.postValue(
                Pair(
                    first = true,
                    second = jewelleryTypeDropDownItems.value!!.second
                )
            )
    }

    // Verify and fetch design and cloth dropdown items.
    fun verifyAndFetchDesignDropDownItems() {
        designDropDownItems.value?.let { dropdownItems ->
            if (!dropdownItems.first)
                viewModelScope.launch {
                    fetchDesignDropDownItems()
                }
        } ?: viewModelScope.launch {
            fetchDesignDropDownItems()
        }
    }

    // Verify and fetch clothing dropdown items.
    fun verifyAndFetchClothDropDownItems() {
        clothDropDownItems.value?.let { dropdownItems ->
            if (!dropdownItems.first)
                viewModelScope.launch {
                    fetchClothDropDownItems()
                }
        } ?: viewModelScope.launch {
            fetchClothDropDownItems()
        }
    }

    // Verify and fetch clothing type dropdown items.
    fun verifyAndFetchClothingTypeDropDownItems() {
        clothingTypeDropDownItems.value?.let { dropdownItems ->
            if (!dropdownItems.first)
                viewModelScope.launch {
                    fetchClothingTypeDropDownItems()
                }
        } ?: viewModelScope.launch {
            fetchClothingTypeDropDownItems()
        }
    }

    // Verify and fetch jewellery type dropdown items.
    fun verifyAndFetchJewelleryTypeDropDownItems() {
        jewelleryTypeDropDownItems.value?.let { dropdownItems ->
            if (!dropdownItems.first)
                viewModelScope.launch {
                    fetchJewelleryTypeDropDownItems()
                }
        } ?: viewModelScope.launch {
            fetchJewelleryTypeDropDownItems()
        }
    }

    private fun getFormProgressRequestBody(
        progressUpload: ProgressUpload,
        field: String
    ): ProgressUpload.ProgressRequestBody =
        progressUpload.ProgressRequestBody(
            MediaType.parse("text/plain"),
            field
        )

    private fun addMultiPartProgressRequestBody(
        progressUpload: ProgressUpload,
        partName: String,
        file: File?
    ) {
        file?.let {
            imageFiles.add(
                MultipartBody.Part.createFormData(
                    partName, it.name, progressUpload.ProgressRequestBody(
                        MediaType.parse("image/*"), content = compressImage(file = it)
                    )
                )
            )
        }
    }

    private fun Boolean.toInt(): Int =
        if (this)
            1
        else
            0

    private fun compressImage(file: File): File {
        // Check if fileLength is less than 3 MB, if so, simply return.
        val fileLength = file.length()
        if (fileLength < 3000000 || fileLength == 0L)
            return file
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val out = file.outputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                getCompressionRatio(fileLength = fileLength),
                out
            )
            out.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun getCompressionRatio(fileLength: Long): Int =
        (Constants.DEFAULT_FILE_SIZE_LIMIT / fileLength).toInt()
}