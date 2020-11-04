package `in`.trendition.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BoutiqueResponse(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("price") val price: String,
    @SerializedName("preparation_time") val preparationTime: String,
    @SerializedName("business_id") val businessId: Int,
    @SerializedName("request_status") val requestStatus: Int
) : Parcelable