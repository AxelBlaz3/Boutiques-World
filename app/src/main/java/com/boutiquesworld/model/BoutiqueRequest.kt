package com.boutiquesworld.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BoutiqueRequest(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String,
    @SerializedName("type_of_dress") val typeOfDress: String,
    @SerializedName("occasion") val occasion: String,
    @SerializedName("time_period") val timePeriod: String,
    @SerializedName("size") val size: String,
    @SerializedName("message") val message: String,
    @SerializedName("upload") val upload: String,
    @SerializedName("COALESCE(boutique_response.request_status, 0)") val requestStatus: Int
) : Parcelable