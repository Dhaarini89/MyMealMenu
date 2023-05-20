package com.android.example.mymealmenu.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.example.mymealmenu.MainActivity
import com.android.example.mymealmenu.MainActivity.Companion.ListStatus
import com.android.example.mymealmenu.R
import com.android.example.mymealmenu.ui.customdialog.AddDishesDialog
import com.android.example.mymealmenu.ui.database.DishesList
import com.android.example.mymealmenu.ui.database.MenuItemList
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class MenuItemAdapter(private val context: Context,private val items: List<String>,private val msg:String,private val viewModel :GenericViewModel) :
    RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {
    var flag :Boolean? =false
    private var listener: OnItemClickListener? = null
    var insideStatus :String =""
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.itemTextView)
        val delete :ImageButton = itemView.findViewById(R.id.delete)
        val edit :ImageButton=itemView.findViewById(R.id.edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)
         if (msg =="add") {
             flag =true
             insideStatus ="DishesList"

        }
        else if (msg=="addTemplate")
         {
            flag =true
            insideStatus ="Template"
         }
        else {
            flag = false
             insideStatus ="MenuList"
        /*    val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem, parent, false)
            return ViewHolder(itemView)

         */
        }
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentItem)
        }
        holder.edit.setOnClickListener {
            if (insideStatus =="DishesList") {
                ListStatus =1
                val customAddDishesDialog =
                    AddDishesDialog(context = context,
                        viewModel,
                        null,
                        null,
                        currentItem,
                        listener!!)
                customAddDishesDialog.show()

            }
            else if (insideStatus =="Template")
            {
                ListStatus =0
                val customAddDishesDialog =
                    AddDishesDialog(context = context,
                        viewModel,
                        null,
                        null,
                        currentItem,
                        listener!!)
                customAddDishesDialog.show()
            }
        }
        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(currentItem)
            // Create a TextView to set the text color
            val message = TextView(context)
            message.text = "\n         Are you sure you want to delete?"
            message.setTextColor(ContextCompat.getColor(context,
                R.color.purple_200))
            builder.setView(message)
            builder.setPositiveButton("Yes") { dialog, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    if (insideStatus =="Template") {
                        viewModel.deleteTemplatebyTemplateName(currentItem)
                    }
                    else if (insideStatus =="DishesList")
                    {
                        viewModel.deletedDishes(currentItem)

                    }
                    listener?.onItemClick(currentItem)
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                // code to handle canceling the exit
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()



        }
        holder.titleTextView.text = currentItem
        if (flag == false)
        {
            holder.delete.visibility =View.INVISIBLE
            holder.edit.visibility =View.INVISIBLE
            if (msg !="addinmenu")
            {
                holder.titleTextView.textAlignment=View.TEXT_ALIGNMENT_CENTER

            }
        }



    }

    override fun getItemCount()= items.size
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface OnItemClickListener {
        fun onItemClick(item: String)
    }
}