package com.android.example.mymealmenu.ui.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "menuitemlist",indices = [Index(value = ["dateString","mealType","itemName"],unique =true)])
data class MenuItemList(
    @PrimaryKey(autoGenerate = true) val id:Int=0 ,
    val dateString :String,
    val mealType :String,
    val itemName :String,
    val dayString :String)

@Entity(tableName = "disheslist",indices = [Index(value = ["itemName"],unique =true)])
data class DishesList(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val itemName: String)

@Entity(tableName = "templatelist")
data class TemplateList(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val templateName :String,
    val dateString :String,
    val mealType :String,
    val itemName :String,
    val dayString :String)
