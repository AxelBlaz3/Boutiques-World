package `in`.trendition.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class SubscriptionPlan(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("plan_name") val planName: String,
    @SerializedName("plan_period") val planPeriod: String,
    @SerializedName("plan_amount") val planAmount: Int
): Parcelable