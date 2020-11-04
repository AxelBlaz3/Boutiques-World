package `in`.trendition.data

import `in`.trendition.model.StoreCategory
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoreCategoryDao {

    /**
     * Get all Cloths.
     */
    @Query("SELECT * FROM Cloth")
    fun getClothes(): List<StoreCategory.Cloth>?

    /**
     * Inserts a list of clothes.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClothes(clothes: List<StoreCategory.Cloth>)

    /**
     * Truncate clothes table.
     */
    @Query("DELETE FROM Cloth")
    fun truncateClothes(): Int

    /**
     * Get all Fabrics.
     */
    @Query("SELECT * FROM Fabric")
    fun getFabrics(): List<StoreCategory.Fabric>?

    /**
     * Inserts a list of fabrics.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFabrics(fabrics: List<StoreCategory.Fabric>)

    /**
     * Truncate Fabric table.
     */
    @Query("DELETE FROM Fabric")
    fun truncateFabrics(): Int

    /**
     * Get all Jewellery items.
     */
    @Query("SELECT * FROM Jewellery")
    fun getJewellery(): List<StoreCategory.Jewellery>?

    /**
     * Inserts a list of Jewellery items.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJewellery(jewellery: List<StoreCategory.Jewellery>)

    /**
     * Truncate Jewellery table.
     */
    @Query("DELETE FROM Jewellery")
    fun truncateJewellery(): Int

    /**
     * Get all DressMaterial items.
     */
    @Query("SELECT * FROM DressMaterial")
    fun getDressMaterials(): List<StoreCategory.DressMaterial>?

    /**
     * Inserts a list of DressMaterial items.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDressMaterials(dressMaterials: List<StoreCategory.DressMaterial>)

    /**
     * Truncate DressMaterial table.
     */
    @Query("DELETE FROM DressMaterial")
    fun truncateDressMaterials(): Int
}