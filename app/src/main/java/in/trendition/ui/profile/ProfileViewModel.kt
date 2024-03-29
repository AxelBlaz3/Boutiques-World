package `in`.trendition.ui.profile

import `in`.trendition.model.*
import `in`.trendition.network.BoutiqueService
import `in`.trendition.repository.ProfileRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileViewModel @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val profileRepository: ProfileRepository
) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> =
        profileRepository.getRetailerMutableLiveData()
    private val boutiqueRequests: MutableLiveData<ArrayList<BoutiqueRequest>> =
        profileRepository.getBoutiqueRequestsMutableLiveData()
    private val boutiqueResponses: MutableLiveData<ArrayList<BoutiqueResponse>> =
        profileRepository.getBoutiqueResponsesMutableLiveData()
    private val subscriptionHistory: MutableLiveData<ArrayList<Subscription>> =
        profileRepository.getSubscriptionHistoryMutableLiveData()
    private val subscriptionPlans: MutableLiveData<ArrayList<SubscriptionPlan>> =
        profileRepository.getSubscriptionPlansMutableLiveData()
    private val payments = profileRepository.getPaymentsMutableLiveData()
    private val razorPayOrderId: MutableLiveData<String?> = profileRepository.getRazorPayOrderId()
    private val isSubscriptionSuccessful: MutableLiveData<Boolean?> =
        profileRepository.getIsSubscriptionSuccessful()
    private val isLogoutSuccessful = profileRepository.getIsLogoutSuccessfulMutableLiveData()
    var isSubscriptionPayment = false
    var subscriptionId: String = ""
    var subscriptionPlan: SubscriptionPlan? = null

    init {
        viewModelScope.launch {
            profileRepository.updateRetailer()
        }
    }

    // BoutiqueRequest form
    private val isPriceEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isPreparationTimeEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isBoutiqueResponsePosted: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val areRequestsLoaded: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getRetailer(): LiveData<Retailer> = retailer
    fun getBoutiqueRequests(): LiveData<ArrayList<BoutiqueRequest>> = boutiqueRequests
    fun getBoutiqueResponses(): LiveData<ArrayList<BoutiqueResponse>> = boutiqueResponses
    fun getIsPriceEmpty(): LiveData<Boolean?> = isPriceEmpty
    fun getIsPreparationTimeEmpty(): LiveData<Boolean?> = isPreparationTimeEmpty
    fun getIsBoutiqueResponsePosted(): LiveData<Boolean?> = isBoutiqueResponsePosted
    fun getAreRequestsLoaded(): LiveData<Boolean> = areRequestsLoaded
    fun getSubscriptionHistory(): LiveData<ArrayList<Subscription>> = subscriptionHistory
    fun getSubscriptionPlans(): LiveData<ArrayList<SubscriptionPlan>> = subscriptionPlans
    fun getRazorPayOrderId(): LiveData<String?> = razorPayOrderId
    fun getIsSubscriptionSuccessful(): LiveData<Boolean?> = isSubscriptionSuccessful
    fun getIsLogoutSuccessful(): LiveData<Boolean?> = isLogoutSuccessful
    fun getPayments(): LiveData<ArrayList<Payment>> = payments

    fun resetIsLogoutSuccessful() {
        isLogoutSuccessful.value = null
    }

    fun logout() {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.logoutRetailer(it)
            }
        }
    }

    fun verifySubscription(
        planId: Int,
        businessId: Int,
        businessName: String,
        uuid: String,
        amount: String,
        paidDate: String,
        endDate: String,
        razorPayOrderId: String,
        razorPayPaymentId: String,
        razorPaySignature: String,
        subscriptionId: String
    ) {
        viewModelScope.launch {
            profileRepository.verifySignature(
                planId,
                businessId,
                businessName,
                uuid,
                amount,
                paidDate,
                endDate,
                razorPayOrderId,
                razorPayPaymentId,
                razorPaySignature,
                subscriptionId
            )
        }
    }

    fun genRazorPayOrderId(orderId: String, price: String) {
        viewModelScope.launch {
            profileRepository.generateRazorPayOrderId(orderId, price)
        }
    }

    fun refreshRetailer() {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.refreshRetailer(it.shopId)
            }
        }
    }

    fun resetRazorPayOrderId() {
        razorPayOrderId.value = null
    }

    fun resetIsSubscriptionSuccessful() {
        isSubscriptionSuccessful.value = null
    }

    fun setIsSubscriptionSuccessful(newValue: Boolean) {
        isSubscriptionSuccessful.value = newValue
    }

    fun updateBoutiqueRequests(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                areRequestsLoaded.value =
                    profileRepository.updateBoutiqueRequests(it.shopId, forceRefresh)
            }
        }
    }

    fun updateBoutiqueResponses(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.updateBoutiqueResponses(
                    businessId = it.shopId,
                    forceRefresh = forceRefresh
                )
            }
        }
    }

    fun updateSubscriptionPlans(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.getSubscriptionPlans(forceRefresh)
            }
        }
    }

    fun updateSubscriptionHistory(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.getSubscriptionHistory(it.shopId, forceRefresh)
            }
        }
    }

    fun updatePayments(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                profileRepository.getPayments(it.shopId, forceRefresh)
            }
        }
    }

    fun resetIsPriceEmpty() {
        isPriceEmpty.value = null
    }

    fun resetIsBoutiqueResponsePosted() {
        isBoutiqueResponsePosted.value = null
    }

    fun resetIsPreparationTimeEmpty() {
        isPreparationTimeEmpty.value = null
    }

    fun submitBoutiqueResponse(
        requestId: Int,
        preparationTime: String,
        price: String,
        requestStatus: Int
    ) {
        isPriceEmpty.value = price.isEmpty() && requestStatus != 2
        isPreparationTimeEmpty.value = preparationTime.isEmpty() && requestStatus != 2
        if (requestStatus != 2 && (price.isEmpty() || preparationTime.isEmpty()))
            return

        retailer.value?.let { retailer ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = boutiqueService.postBoutiqueResponse(
                        requestId,
                        preparationTime,
                        price,
                        retailer.shopId,
                        requestStatus
                    ).execute()

                    if (response.code() == 201) {
                        isBoutiqueResponsePosted.postValue(true)
                        updateBoutiqueRequests(forceRefresh = true)
                        updateBoutiqueResponses(forceRefresh = true)
                    } else
                        isBoutiqueResponsePosted.postValue(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isBoutiqueResponsePosted.postValue(false)
            }
        }
    }

    fun makeFreeSubscription(
        planId: Int,
        planAmount: Int,
        paidDate: String,
        endDate: String,
        subscriptionId: String
    ) {
        retailer.value?.let { retailer ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = boutiqueService.subscribeBoutique(
                        planId,
                        retailer.shopId.toString(),
                        retailer.businessName,
                        retailer.uuid,
                        planAmount.toString(),
                        paidDate,
                        endDate,
                        "STARTER_PLAN",
                        "STARTER_PLAN",
                        subscriptionId
                    ).execute()
                    if (response.code() == 204 || response.code() == 200) {
                        isSubscriptionSuccessful.postValue(true)
                        return@launch
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isSubscriptionSuccessful.postValue(false)
            }
        }
    }
}