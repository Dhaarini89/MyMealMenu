package com.android.example.mymealmenu.ui.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface mealMenuDao {

    @Query("Update MenuItemList set dateString = :dateString where dayString= :dayString and dateString ='default'")
    suspend fun updatedateString(dateString: String,dayString: String)

    @Query("Update TemplateList set templateName = :templateName where templateName = :oldtemplateName")
    suspend fun updateTemplateName(oldtemplateName :String,templateName :String)

    @Query("Update DishesList set itemName = :itemName where itemName = :olditemName")
    suspend fun updateItems(itemName :String,olditemName :String)

    @Query("Update MenuItemList set itemName = :itemName where itemName = :olditemName")
    suspend fun updateItemsinMenu(itemName :String,olditemName :String)

    @Query("Insert into MenuItemList(dateString,dayString,mealType,itemName) select 'default'||dateString,dayString,mealType,itemName from MenuItemList where dateString in (:values)")
    suspend fun insertfromTemplate(values:String )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertmenu(menuitemlist: MenuItemList)

    @Insert()
    suspend fun insertdishes(dishesList: DishesList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserttemplate(templateList: TemplateList)

    @Query("select * from disheslist")
    suspend fun getDishesRecords(): List<DishesList>

    @Query("Select Distinct templateName from templatelist")
    suspend fun getTemplateNames() : List<String>


    @Query("SELECT * FROM menuitemlist where dateString =:dateString order by dateString,mealType")
    suspend fun getMenuForTemplate( dateString :String): List<MenuItemList>

    @Query("SELECT * FROM TemplateList where templateName =:templateName")
    suspend fun getMenuFromTemplate( templateName :String): List<MenuItemList>

    @Query("select * from menuitemlist")
    suspend fun getMenuRecords(): List<MenuItemList>

    @Query("SELECT itemName FROM menuitemlist where dateString =:dateString and mealType =:mealType")
    suspend fun getMenuonDateMealType( dateString :String, mealType :String,): List<String>


    @Query("DELETE FROM menuitemList WHERE dateString = :dateString")
    suspend fun deleteByDateString(dateString: String)

    @Query("Delete from dishesList where itemName=:itemName")
    suspend fun deleteBydishesList(itemName: String)

    @Query("Delete FROM menuitemlist where dateString =:dateString and mealType =:mealType and itemName =:itemName")
    suspend fun deleteMenuItem(itemName: String, dateString :String, mealType :String)

    @Query("Delete from dishesList ")
    suspend fun deletedishes()

    @Query("Delete from templateList")
    suspend fun deleteTemplates()

    @Query("Delete from MenuItemList")
    suspend fun deleteMenuItemsList()

    @Query("delete from menuItemList where itemName = :itemName")
    suspend fun deleteMenuwithdishes(itemName: String)

    @Query("Delete from TemplateList where templateName= :templateName")
    suspend fun deleteTemplatebyTemplateName(templateName: String)


}


@Database(entities = [DishesList::class,MenuItemList::class,TemplateList::class], version = 1, exportSchema = false)
abstract class DatabaseMealMenu :RoomDatabase() {
    abstract val mealMenuDao : mealMenuDao
}


