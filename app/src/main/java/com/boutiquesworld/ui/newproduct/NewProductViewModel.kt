package com.boutiquesworld.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.network.BoutiqueService
import com.boutiquesworld.repository.RetailerRepository
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
    retailerRepository: RetailerRepository,
    private val boutiqueService: BoutiqueService
) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> =
        retailerRepository.getRetailerMutableLiveData()
    private val isProductNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductTypeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductClothEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isProductFabricEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
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

    // Know if the product submission is successful
    private val isProductSubmissionSuccessful: MutableLiveData<Boolean> = MutableLiveData()

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
    fun getIsProductSubmissionSuccessful(): LiveData<Boolean> = isProductSubmissionSuccessful

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
                val isDataValid = isDataValid(
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

    private fun isDataValid(
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

    private fun addFileMultiPartBody(partName: String, file: File?) {
        file?.let {
            val requestBody = RequestBody.create(MediaType.parse("image/*"), it)
            imageFiles.add(MultipartBody.Part.createFormData(partName, it.name, requestBody))
        }
    }

    private fun getFormRequestBody(field: String): RequestBody =
        RequestBody.create(MediaType.parse("text/plain"), field)
}