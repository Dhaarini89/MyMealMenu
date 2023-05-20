package com.android.example.mymealmenu.ui.customdialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.databinding.FragmentListBinding
import com.android.example.mymealmenu.databinding.MenuitemsBinding
import com.android.example.mymealmenu.databinding.NewdishBinding
import com.android.example.mymealmenu.databinding.TemplateListBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import com.android.example.mymealmenu.ui.database.DishesList
import com.android.example.mymealmenu.ui.database.MenuItemList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Add4mTemplateDialog(context: Context, viewModel: GenericViewModel) : Dialog(context) {
    private lateinit var binding : TemplateListBinding
    private var listener: On4mTemplateSelectedListener? = null
    var itemsList :List<String> = listOf()

    init {
        binding = TemplateListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        GlobalScope.launch(Dispatchers.Main) {

            val values = viewModel.getTemplateNames()
            if (values.isEmpty()) {
                Toast.makeText(context,
                    "There is no templates currently created",
                    Toast.LENGTH_LONG).show()
            }
                val adapter = MenuItemAdapter(context,values, "add",viewModel)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            adapter.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    listener!!.on4mTemplateSelected(item)
                    dismiss()

                }
            })
        }
            binding.Cancel.setOnClickListener {
            dismiss()
        }


    }


    interface On4mTemplateSelectedListener {
        fun on4mTemplateSelected(template: String)
    }

    fun setOnItemSelectedListener(listener: On4mTemplateSelectedListener) {
        this.listener = listener
    }

}
