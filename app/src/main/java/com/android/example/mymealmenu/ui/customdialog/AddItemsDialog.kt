package com.android.example.mymealmenu.ui.customdialog

import android.app.Dialog
import android.content.Context
import android.nfc.Tag
import android.text.TextUtils.split
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.databinding.MenuitemsBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import com.android.example.mymealmenu.ui.database.DishesList
import com.android.example.mymealmenu.ui.database.MenuItemList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddItemsDialog(context: Context, viewModel: GenericViewModel, Tag:String, lifecycleCoroutineScope: LifecycleCoroutineScope)
    : Dialog(context),AddDishesDialog.OnTemplateSelectedListener {
    private lateinit var binding :MenuitemsBinding
    private var listener: OnItemSelectedListener? = null
    var itemList :List<String> = listOf()
    var inviewModel :GenericViewModel? =null
    var inTag :String? =null
    var inlifecycleCoroutineScope :LifecycleCoroutineScope?=null
    init {
        binding =MenuitemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        inviewModel=viewModel
        inTag=Tag
        inlifecycleCoroutineScope=lifecycleCoroutineScope

        loadingRecyclerView()



        binding.Cancel.setOnClickListener {
            dismiss()
        }
        binding.AddNItem.setOnClickListener {
            val customAddDishesDialog =AddDishesDialog(context,viewModel,null,binding,null,null)
            customAddDishesDialog.setOnItemSelectedListener(this@AddItemsDialog)
            customAddDishesDialog.show()

        }

    }

    fun loadingRecyclerView()
    {
        GlobalScope.launch(Dispatchers.Main) {
            val values = inviewModel!!.getDishes()
            itemList =values.map(DishesList::itemName)
            val adapter = MenuItemAdapter(context,itemList, "addinmenu", inviewModel!! )
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            val mealtype = inTag!!.split(",")[2]
            val dateString =inTag!!.split(",")[1]
            val day =inTag!!.split(",")[0]
            adapter.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    listener!!.onItemSelected(item)
                    inlifecycleCoroutineScope!!.launch {
                        inviewModel!!.insertMenu(MenuItemList(dateString = dateString,
                            mealType = mealtype,
                            itemName = item.replaceFirstChar(Char::uppercase),
                            dayString =day ))
                    }
                    Log.d("stages","Inserted")
                    dismiss()

                }
            })

        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    override fun onTemplateSelected(template: String) {
        Log.d("HereAddItems","here")
           loadingRecyclerView()
    }

}