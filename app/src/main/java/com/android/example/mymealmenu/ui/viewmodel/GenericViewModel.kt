package com.android.example.mymealmenu.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.android.example.mymealmenu.ui.database.DishesList
import com.android.example.mymealmenu.ui.database.MealMenuRepository
import com.android.example.mymealmenu.ui.database.MenuItemList
import com.android.example.mymealmenu.ui.database.TemplateList

class GenericViewModel : ViewModel() {

    private val mealMenuRepository = MealMenuRepository.get()

    suspend fun insertMenu(menuItemList: MenuItemList)
    {
        mealMenuRepository.insertmenu(menuItemList)
    }

    suspend fun updateItemNames(itemName :String,olditemName :String)
    {
        mealMenuRepository.updateItemName(itemName,olditemName)
    }
    suspend fun getMenuFromTemplate(TemplateName :String) :List<MenuItemList>
    {
        return mealMenuRepository.getMenuFromTemplate(TemplateName)
    }
    suspend fun insertMenuFromTemplate(values :String)
    {
        mealMenuRepository.insertMenufromTemplate(values)
    }

    suspend fun deleteDishes()
    {
        mealMenuRepository.deleteDishesList()
    }
    suspend fun updateTemplateName(oldTemplateName: String,templateName: String)
    {
        mealMenuRepository.updateTemplateName(oldTemplateName,templateName)
    }
    suspend fun deleteTemplatebyTemplateName(templateName: String)
    {
        mealMenuRepository.deleteTemplatebyTemplateName(templateName)
    }
    suspend fun deleteTemplates()
    {
        mealMenuRepository.deleteTemplates()
    }

    suspend fun deleteMenuItem(itemName: String,dateString: String,menuType :String)
    {
        mealMenuRepository.DeleteMenuItem(itemName,dateString,menuType)
    }
    suspend fun deletedDishes(itemName: String)
    {
        mealMenuRepository.DeleteDishes(itemName)
    }



    suspend fun getTemplateNames() :List<String>
    {
        return mealMenuRepository.getTemplateNames()
    }
    suspend fun insertTemplate(templateList: TemplateList)
    {
        mealMenuRepository.inserttemplate(templateList)
    }
    suspend fun deletemenusthisweek(deleteString: String)
    {
        mealMenuRepository.deletemenusthisweek(deleteString)
    }
    suspend fun getMenu() :List<MenuItemList>
    {
        return mealMenuRepository.getMenuRecords()
    }
    suspend fun updatedateString(dateString: String,dayString: String)
    {
        mealMenuRepository.updatedateString(dateString,dayString)
    }
    suspend fun getDishes() :List<DishesList>
    {
        return mealMenuRepository.getDishesList()
    }

    suspend fun getMenuForTemplate(dateString: String) :List<MenuItemList>
    {
        return mealMenuRepository.getMenuForTemplate(dateString)
    }
    suspend fun getMenuonDateMealType( dateString :String, mealType :String,) :List<String>
    {
        return mealMenuRepository.getMenuonDateMealType( dateString ,mealType )
    }

    suspend fun insertDishes(dishesList: DishesList) :Boolean
    {
        return mealMenuRepository.insertDishes(dishesList)
    }
}