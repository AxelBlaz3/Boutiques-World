package `in`.trendition.ui.address

import `in`.trendition.model.Address
import `in`.trendition.model.OrderAddress
import `in`.trendition.network.BoutiqueService
import `in`.trendition.repository.AddressRepository
import `in`.trendition.repository.ProfileRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressViewModel @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val addressRepository: AddressRepository,
    profileRepository: ProfileRepository
) :
    ViewModel() {
    // Address list
    private val addressList: MutableLiveData<ArrayList<Address>> =
        addressRepository.getAddressMutableLiveData()
    private val orderAddressList: MutableLiveData<ArrayList<OrderAddress>> =
        addressRepository.getOrderAddressMutableLiveData()

    private var razorPayOrderId: MutableLiveData<String> =
        addressRepository.getRazorPayOrderIdMutableLiveData()

    private val retailer = profileRepository.getRetailerMutableLiveData()
    private val isPaymentCaptured: MutableLiveData<Boolean?> = MutableLiveData()

    // Variables for verifying whether fields are empty
    private val addressNameEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressMobileEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressPincodeEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressFlatEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressAreaEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressLandmarkEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressTownEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val addressStateEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isAddressPosted: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isOrderAddressPosted: MutableLiveData<Boolean?> = MutableLiveData(null)

    // Map for posting address to server
    private val addressMap: HashMap<String, RequestBody> = HashMap()

    fun getAddressName(): LiveData<Boolean> = addressNameEmpty
    fun getAddressMobile(): LiveData<Boolean> = addressMobileEmpty
    fun getAddressPincode(): LiveData<Boolean> = addressPincodeEmpty
    fun getAddressFlat(): LiveData<Boolean> = addressFlatEmpty
    fun getAddressArea(): LiveData<Boolean> = addressAreaEmpty
    fun getAddressLandmark(): LiveData<Boolean> = addressLandmarkEmpty
    fun getAddressTown(): LiveData<Boolean> = addressTownEmpty
    fun getAddressState(): LiveData<Boolean> = addressStateEmpty
    fun getIsAddressPosted(): LiveData<Boolean?> = isAddressPosted
    fun getIsOrderAddressPosted(): LiveData<Boolean?> = isOrderAddressPosted
    fun getAddressList(): LiveData<ArrayList<Address>> = addressList
    fun getOrderAddressList(): LiveData<ArrayList<OrderAddress>> = orderAddressList
    fun getRazorPayOrderId(): LiveData<String> = razorPayOrderId
    fun getIsPaymentCaptured(): LiveData<Boolean?> = isPaymentCaptured

    fun genRazorPayOrderId(orderId: String, price: Int, address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            postOrderAddress(address)
            addressRepository.genRazorPayOrderIdAndUpdateCartWithOrderId(
                retailer.value!!.shopId.toString(),
                orderId,
                price
            )
        }
    }

    fun resetIsPosted() {
        isAddressPosted.value = null
    }

    fun resetIsOrderAddressPosted() {
        isOrderAddressPosted.value = null
    }

    fun resetIsPaymentCaptured() {
        isPaymentCaptured.value = null
    }

    fun resetRazorPayOrderId() {
        razorPayOrderId.value = ""
    }

    fun setIsPaymentCaptured(newValue: Boolean) {
        isPaymentCaptured.value = newValue
    }

    fun clearAddressList() {
        addressList.value?.clear()
    }

    fun verifyAndCapturePayment(
        razorPayOrderId: String,
        paymentId: String,
        signature: String,
        orderId: String,
        cartCount: Int,
        amount: String
    ) {
        viewModelScope.launch {
            isPaymentCaptured.value = addressRepository.verifyAndCapturePayment(
                razorPayOrderId,
                paymentId,
                signature,
                orderId,
                retailer.value!!.shopId.toString(),
                cartCount,
                amount
            )
        }
    }

    fun updateAddressList(userId: Int, forceRefresh: Boolean) {
        viewModelScope.launch {
            addressRepository.updateAddressList(userId, forceRefresh)
        }
    }

    fun updateOrderAddressList(userId: Int, forceRefresh: Boolean) {
        viewModelScope.launch {
            addressRepository.updateOrderAddressList(userId, forceRefresh)
        }
    }

    fun postAddress(
        addressName: String,
        addressMobile: String,
        addressPincode: String,
        addressFlat: String,
        addressArea: String,
        addressLandmark: String,
        addressTown: String,
        addressState: String,
        userId: String,
        orderId: String
    ) {
        if (isAddressValid(
                addressName,
                addressMobile,
                addressPincode,
                addressFlat,
                addressArea,
                addressLandmark,
                addressTown,
                addressState,
                userId,
                orderId
            )
        ) {
            Log.d(this.javaClass.simpleName, orderId)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = boutiqueService.postAddress(addressMap).execute()
                    if (response.isSuccessful) {
                        isAddressPosted.postValue(true)
                        return@launch
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isAddressPosted.postValue(false)
            }
        }
    }

    private fun postOrderAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = boutiqueService.postOrderAddress(address).execute()
                if (response.isSuccessful) {
                    isOrderAddressPosted.postValue(true)
                    return@launch
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isOrderAddressPosted.postValue(false)
        }
    }

    // Check whether any address field is empty.
    private fun isAddressValid(
        addressName: String,
        addressMobile: String,
        addressPincode: String,
        addressFlat: String,
        addressArea: String,
        addressLandmark: String,
        addressTown: String,
        addressState: String,
        userId: String,
        orderId: String
    ): Boolean {
        addressNameEmpty.value = addressName.isEmpty()
        if (addressNameEmpty.value!!)
            return false
        addressMobileEmpty.value = addressMobile.isEmpty()
        if (addressMobileEmpty.value!!)
            return false
        addressPincodeEmpty.value = addressPincode.isEmpty()
        if (addressPincodeEmpty.value!!)
            return false
        addressFlatEmpty.value = addressFlat.isEmpty()
        if (addressFlatEmpty.value!!)
            return false
        addressAreaEmpty.value = addressArea.isEmpty()
        if (addressAreaEmpty.value!!)
            return false
        addressLandmarkEmpty.value = addressLandmark.isEmpty()
        if (addressLandmarkEmpty.value!!)
            return false
        addressTownEmpty.value = addressTown.isEmpty()
        if (addressTownEmpty.value!!)
            return false
        addressStateEmpty.value = addressState.isEmpty()
        if (addressStateEmpty.value!!)
            return false

        // All checks done, assign data to map
        addressMap["fullname"] = getFormRequestBody(addressName)
        addressMap["mobile"] = getFormRequestBody(addressMobile)
        addressMap["pincode"] = getFormRequestBody(addressPincode)
        addressMap["flat"] = getFormRequestBody(addressFlat)
        addressMap["area"] = getFormRequestBody(addressArea)
        addressMap["landmark"] = getFormRequestBody(addressLandmark)
        addressMap["city"] = getFormRequestBody(addressTown)
        addressMap["state"] = getFormRequestBody(addressState)
        addressMap["user_id"] = getFormRequestBody(userId)
        addressMap["order_id"] = getFormRequestBody(orderId)

        return true
    }

    private fun getFormRequestBody(field: String): RequestBody =
        RequestBody.create(MediaType.parse("text/plain"), field)
}