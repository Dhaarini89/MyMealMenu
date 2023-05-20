package com.android.example.mymealmenu.ui.customdialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.MainActivity.Companion.ListStatus
import com.android.example.mymealmenu.databinding.FragmentListBinding
import com.android.example.mymealmenu.databinding.MenuitemsBinding
import com.android.example.mymealmenu.databinding.NewdishBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import com.android.example.mymealmenu.ui.database.DishesList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddDishesDialog(context: Context, viewModel: GenericViewModel, Mainbinding :FragmentListBinding?, Menubinding:MenuitemsBinding?,tag :String?,menulistener: MenuItemAdapter.OnItemClickListener?)
    : Dialog(context) {
        private lateinit var binding : NewdishBinding
        private var listener: OnTemplateSelectedListener? = null
       var itemsList :List<String> = listOf()

        init {
            binding = NewdishBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            Log.d("Dishes",context.toString())
            if (tag !=null)
            {
                if (ListStatus == 1) {
                    binding.textInputLayout.setHint("Update the itemName")
                }
                else
                {
                    binding.textInputLayout.setHint("Update the templateName")

                }
                binding.textInputEditText.setText(tag)

            }

            if (Mainbinding == null && Menubinding == null && tag == null)
            {
                binding.textInputLayout.setHint("Enter a template Name")
            }
            binding.Cancel.setOnClickListener {
                dismiss()
            }
            binding.AddNItem.setOnClickListener {
                if (tag!=null)
                {
                  val updatedItem =binding.textInputEditText.text.toString().replaceFirstChar(Char::uppercase)
                  GlobalScope.launch(Dispatchers.IO) {
                     if (ListStatus ==1) {
                         viewModel.updateItemNames(updatedItem, tag)
                         menulistener!!.onItemClick("Edit")

                     }
                      else
                     {
                         viewModel.updateTemplateName(tag,updatedItem)
                         menulistener!!.onItemClick("Edit")

                     }
                      dismiss()
                  }
                }
                else if (Mainbinding == null && Menubinding == null && tag == null)
                {
                    val template =binding.textInputEditText.text.toString()
                    listener?.onTemplateSelected(template)
                    dismiss()
                }
                else
                {
                GlobalScope.launch(Dispatchers.Main) {
                    val itemname = binding.textInputEditText.text.toString()
                    val items = DishesList(itemName = itemname.replaceFirstChar(Char::uppercase))
                    val flag = viewModel.insertDishes(items)
                    if (flag == true) {
                        val values = viewModel.getDishes()
                        itemsList = values.map(DishesList::itemName)
                        if (Mainbinding != null) {
                              listener?.onTemplateSelected("value")

                        } else if (Menubinding != null) {
                            listener?.onTemplateSelected("value")
                        }
                        dismiss()
                    } else {
                        binding.textInputEditText.setError("Item already exist")
                    }
                }
                     }
            }

        }


        interface OnTemplateSelectedListener {
            fun onTemplateSelected(template: String)
        }

        fun setOnItemSelectedListener(listener: OnTemplateSelectedListener) {
            this.listener = listener
        }

    }
