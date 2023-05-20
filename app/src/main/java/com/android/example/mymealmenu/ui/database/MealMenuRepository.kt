package com.android.example.mymealmenu.ui.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MealMenuRepository private constructor(context: Context){
    private val mealMenuDatabase : DatabaseMealMenu = Room.databaseBuilder(
        context.applicationContext,
        DatabaseMealMenu::class.java,
        "databaseMealMenu"
    )
        .build()

    suspend fun insertDishes(dishesList: DishesList) : Boolean {
        try {
            mealMenuDatabase.mealMenuDao.insertdishes(dishesList)
            return true
        } catch (e: SQLiteConstraintException)
        {
            Log.d("MenuException",e.toString())
            return false
        }
    }

    suspend fun getDishesList() :List<DishesList>
    {
        return mealMenuDatabase.mealMenuDao.getDishesRecords()
    }
    suspend fun deletemenusthisweek(deleteString: String)
    {
        mealMenuDatabase.mealMenuDao.deleteByDateString(deleteString)
    }
   suspend fun inserttemplate(templateList: TemplateList)
    {
         mealMenuDatabase.mealMenuDao.inserttemplate(templateList)
    }

    suspend fun updatedateString(dateString: String,dayString: String)
    {
        mealMenuDatabase.mealMenuDao.updatedateString(dateString,dayString)
    }

    suspend fun getMenuForTemplate(dateString: String) :List<MenuItemList>
    {
        return mealMenuDatabase.mealMenuDao.getMenuForTemplate(dateString)
    }
    suspend fun insertMenufromTemplate(value :String)
    {
        mealMenuDatabase.mealMenuDao.insertfromTemplate(value)
    }

    suspend fun updateItemName(itemName :String,olditemName :String)
    {
        mealMenuDatabase.mealMenuDao.updateItems(itemName,olditemName)
        mealMenuDatabase.mealMenuDao.updateItemsinMenu(itemName,olditemName)
    }

    suspend fun getTemplateNames() :List<String>
    {
       return mealMenuDatabase.mealMenuDao.getTemplateNames()
    }

    suspend fun deleteTemplates()
    {
        mealMenuDatabase.mealMenuDao.deleteTemplates()
    }

    suspend fun deleteDishesList()
    {
        mealMenuDatabase.mealMenuDao.deletedishes()
        mealMenuDatabase.mealMenuDao.deleteMenuItemsList()
    }
    suspend fun DeleteDishes(itemName :String)
    {
        mealMenuDatabase.mealMenuDao.deleteBydishesList(itemName)
        mealMenuDatabase.mealMenuDao.deleteMenuwithdishes(itemName)
    }

    suspend fun updateTemplateName(oldTemplateName :String,templateName: String)
    {
        mealMenuDatabase.mealMenuDao.updateTemplateName(oldTemplateName,templateName)
    }
    suspend fun deleteTemplatebyTemplateName(templateName: String)
    {
        mealMenuDatabase.mealMenuDao.deleteTemplatebyTemplateName(templateName)
    }
    suspend fun DeleteMenuItem(itemName: String,dateString: String,menuType :String)
    {
        mealMenuDatabase.mealMenuDao.deleteMenuItem(itemName,dateString,menuType)
    }
    suspend fun getMenuFromTemplate(templateName :String) :List<MenuItemList>
    {
        return mealMenuDatabase.mealMenuDao.getMenuFromTemplate(templateName)
    }
    suspend fun insertmenu(menuitemlist: MenuItemList)
    {
        try {
            mealMenuDatabase.mealMenuDao.insertmenu(menuitemlist)
        }
        catch (ex:Exception)
        {
            Log.d("Constraints on trying to Insert",ex.toString())
        }
    }

    suspend fun getMenuonDateMealType( dateString :String, mealType :String,) :List<String>
    {
        return mealMenuDatabase.mealMenuDao.getMenuonDateMealType( dateString ,mealType )
    }


    suspend fun getMenuRecords(): List<MenuItemList>
    {
       return mealMenuDatabase.mealMenuDao.getMenuRecords()
    }


    companion object {
        private var INSTANCE : MealMenuRepository? = null
        fun initalize(context: Context)
        {
            if (INSTANCE == null)
            {
                INSTANCE = MealMenuRepository(context)
            }
        }

        fun get() : MealMenuRepository {
            return INSTANCE ?:
            throw IllegalStateException("Refuel Repository must be initalized")
        }
    }
}