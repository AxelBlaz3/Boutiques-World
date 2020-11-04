package `in`.trendition.ui.product

import `in`.trendition.databinding.ProductItemBinding
import `in`.trendition.model.StoreCategory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private val listener: StoreAdapterListener) :
    ListAdapter<StoreCategory, StoreAdapter.StoreCategoryViewHolder>(StoreCategoryDiffUtil) {

    interface StoreAdapterListener {
        fun onStoreItemClick(storeCategory: StoreCategory, position: Int)
    }

    object StoreCategoryDiffUtil : DiffUtil.ItemCallback<StoreCategory>() {
        override fun areItemsTheSame(oldItem: StoreCategory, newItem: StoreCategory): Boolean =
            if (oldItem is StoreCategory.Fabric && newItem is StoreCategory.Fabric)
                oldItem.productId == newItem.productId
            else if (oldItem is StoreCategory.Jewellery && newItem is StoreCategory.Jewellery)
                oldItem.productId == newItem.productId
            else if (oldItem is StoreCategory.Cloth && newItem is StoreCategory.Cloth)
                oldItem.productId == newItem.productId
            else
                (oldItem as StoreCategory.DressMaterial).productId == (newItem as StoreCategory.DressMaterial).productId

        override fun areContentsTheSame(oldItem: StoreCategory, newItem: StoreCategory): Boolean =
            if (oldItem is StoreCategory.Fabric && newItem is StoreCategory.Fabric)
                oldItem.availableQuantity == newItem.availableQuantity &&
                        oldItem.businessCategory == newItem.businessCategory &&
                        oldItem.businessCategoryType == newItem.businessCategoryType &&
                        oldItem.productCategory == newItem.productCategory &&
                        oldItem.productThumb == newItem.productThumb &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.deliveryTime == newItem.deliveryTime &&
                        oldItem.productType == newItem.productType &&
                        oldItem.minimumQuantity == newItem.minimumQuantity &&
                        oldItem.productFabric == newItem.productFabric &&
                        oldItem.productCloth == newItem.productCloth &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.productWeight == newItem.productWeight &&
                        oldItem.productWidth == newItem.productWidth &&
                        oldItem.productPattern == newItem.productPattern &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.businessId == newItem.businessId &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.upid == newItem.upid &&
                        oldItem.likes == newItem.likes &&
                        oldItem.date == newItem.date &&
                        oldItem.productStatus == newItem.productStatus
            else if (oldItem is StoreCategory.Jewellery && newItem is StoreCategory.Jewellery)
                oldItem.availableQuantity == newItem.availableQuantity &&
                        oldItem.businessCategory == newItem.businessCategory &&
                        oldItem.businessCategoryType == newItem.businessCategoryType &&
                        oldItem.productCategory == newItem.productCategory &&
                        oldItem.productThumb == newItem.productThumb &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.deliveryTime == newItem.deliveryTime &&
                        oldItem.productType == newItem.productType &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.productMaterial == newItem.productMaterial &&
                        oldItem.gender == newItem.gender &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.businessId == newItem.businessId &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.upid == newItem.upid &&
                        oldItem.likes == newItem.likes &&
                        oldItem.date == newItem.date &&
                        oldItem.productStatus == newItem.productStatus
            else if (oldItem is StoreCategory.Cloth && newItem is StoreCategory.Cloth)
                oldItem.availableQuantity == newItem.availableQuantity &&
                        oldItem.businessCategory == newItem.businessCategory &&
                        oldItem.businessCategoryType == newItem.businessCategoryType &&
                        oldItem.productCategory == newItem.productCategory &&
                        oldItem.productThumb == newItem.productThumb &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.deliveryTime == newItem.deliveryTime &&
                        oldItem.productType == newItem.productType &&
                        oldItem.productFabric == newItem.productFabric &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.productCloth == newItem.productCloth &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productOccasion == newItem.productOccasion &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.sizeS == newItem.sizeS &&
                        oldItem.sizeM == newItem.sizeM &&
                        oldItem.sizeL == newItem.sizeL &&
                        oldItem.sizeXL == newItem.sizeXL &&
                        oldItem.sizeXXL == newItem.sizeXXL &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.businessId == newItem.businessId &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.upid == newItem.upid &&
                        oldItem.likes == newItem.likes &&
                        oldItem.date == newItem.date &&
                        oldItem.productStatus == newItem.productStatus
            else
                (oldItem as StoreCategory.DressMaterial).availableQuantity == (newItem as StoreCategory.DressMaterial).availableQuantity &&
                        oldItem.businessCategory == newItem.businessCategory &&
                        oldItem.businessCategoryType == newItem.businessCategoryType &&
                        oldItem.productCategory == newItem.productCategory &&
                        oldItem.productThumb == newItem.productThumb &&
                        oldItem.productImage1 == newItem.productImage1 &&
                        oldItem.productImage2 == newItem.productImage2 &&
                        oldItem.productImage3 == newItem.productImage3 &&
                        oldItem.productImage4 == newItem.productImage4 &&
                        oldItem.productImage5 == newItem.productImage5 &&
                        oldItem.businessName == newItem.businessName &&
                        oldItem.deliveryTime == newItem.deliveryTime &&
                        oldItem.productType == newItem.productType &&
                        oldItem.productFabric == newItem.productFabric &&
                        oldItem.productCloth == newItem.productCloth &&
                        oldItem.productName == newItem.productName &&
                        oldItem.productPrice == newItem.productPrice &&
                        oldItem.productDescription == newItem.productDescription &&
                        oldItem.productWeight == newItem.productWeight &&
                        oldItem.setPiece == newItem.setPiece &&
                        oldItem.topMeasurement == newItem.topMeasurement &&
                        oldItem.bottomMeasurement == newItem.bottomMeasurement &&
                        oldItem.dupattaMeasurement == newItem.dupattaMeasurement &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.productPattern == newItem.productPattern &&
                        oldItem.productColor == newItem.productColor &&
                        oldItem.businessId == newItem.businessId &&
                        oldItem.uuid == newItem.uuid &&
                        oldItem.upid == newItem.upid &&
                        oldItem.likes == newItem.likes &&
                        oldItem.date == newItem.date &&
                        oldItem.productStatus == newItem.productStatus
    }

    class StoreCategoryViewHolder(private val binding: ProductItemBinding, private val listener: StoreAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storeCategory: StoreCategory, position: Int) {
            binding.run {
                when (storeCategory) {
                    is StoreCategory.Fabric -> fabric = storeCategory
                    is StoreCategory.Cloth -> clothing = storeCategory
                    is StoreCategory.Jewellery -> jewellery = storeCategory
                    is StoreCategory.DressMaterial -> dressMaterial = storeCategory
                }
                this.position = position
                this.storeAdapterListener = this@StoreCategoryViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreCategoryViewHolder =
        StoreCategoryViewHolder(
            ProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: StoreCategoryViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}