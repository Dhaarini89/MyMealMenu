
package com.android.example.mymealmenu.ui.planner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.RippleDrawable

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.MainActivity.Companion.Daysvalue
import com.android.example.mymealmenu.MainActivity.Companion.calendar
import com.android.example.mymealmenu.R
import com.android.example.mymealmenu.databinding.FragmentPlannerBinding
import com.android.example.mymealmenu.databinding.MenulayoutBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.customdialog.Add4mTemplateDialog
import com.android.example.mymealmenu.ui.customdialog.AddDishesDialog
import com.android.example.mymealmenu.ui.customdialog.AddItemsDialog
import com.android.example.mymealmenu.ui.customdialog.EditItemsDialog
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import com.android.example.mymealmenu.ui.database.MenuItemList
import com.android.example.mymealmenu.ui.database.TemplateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PlannerFragment : Fragment(), EditItemsDialog.OnEditItemSelectedListener,AddItemsDialog.OnItemSelectedListener,AddDishesDialog.OnTemplateSelectedListener,Add4mTemplateDialog.On4mTemplateSelectedListener {
    private var _binding: FragmentPlannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences
    var views = listOf<MenulayoutBinding>()
    var startDate :Date =Date()
    var endDate :Date =Date()

    var nextDate :Date= Date()
    var adapter :MenuItemAdapter? =null
    var datevalue = mutableListOf<String>()
    var tagdata :String =""
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val dateOnlyFormat = SimpleDateFormat("d",Locale.getDefault())
    val originalviewsTexts =listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
    var viewsTexts = listOf<String>()

    private val viewModel : GenericViewModel by viewModels()

    override fun onEditItemSelected(item: String) {
        for (i in datevalue.indices)
        {
            loadingRecyclerViews(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun on4mTemplateSelected(template: String) {

        lifecycleScope.launch {
            val menuItemList=viewModel.getMenuFromTemplate(template)
            for (menuItem in menuItemList) {
                val menuValue = MenuItemList(mealType = menuItem.mealType, itemName =menuItem.itemName.replaceFirstChar(Char::uppercase), dateString ="default", dayString = menuItem.dayString )
                viewModel.insertMenu(menuValue)
            }
            for (i in datevalue.indices)
            {
                viewModel.updatedateString(datevalue[i],viewsTexts[i])
                loadingRecyclerViews(i)

            }


        }

    }
    override fun onTemplateSelected(template: String) {
        lifecycleScope.launch {
            for (i in views.indices) {
                val menuItemList =viewModel.getMenuForTemplate(datevalue[i])
                for (menuItem in menuItemList)
                {
                    val templateList = TemplateList(templateName = template.replaceFirstChar(Char::uppercase), dateString = menuItem.dateString, dayString = menuItem.dayString, itemName = menuItem.itemName.replaceFirstChar(Char::uppercase), mealType = menuItem.mealType)
                    viewModel.insertTemplate(templateList)
                }
            }
        }
    }
    override fun onItemSelected(item: String) {
        //mealday.put(tagdata,item)
        Log.d("PlannerFragment","here")
        val mealtype = tagdata.split(",")[2]
        val dateString =tagdata.split(",")[1]
        val tag =tagdata.split(",")[0]
        Log.d("tagvalues",mealtype+dateString)
        for (i in viewsTexts.indices) {
            if (viewsTexts[i] == tag) {
                GlobalScope.launch(Dispatchers.Main) {
                    val itemList = viewModel.getMenuonDateMealType(dateString, mealtype)
                    Log.d("Stageshere", dateString+mealtype+itemList.toString())
                    val adapter = MenuItemAdapter(requireContext(),itemList, "display",viewModel)
                    when(mealtype)
                    {
                        "Breakfast" -> {
                            views[i].notificationLayout.recyclerView.adapter = adapter
                            views[i].notificationLayout.recyclerView.layoutManager =
                                LinearLayoutManager(requireContext())

                                    Log.d("StagesAfterAdd", itemList.toString())

                        }

                        "Lunch" ->
                        {
                            views[i].notificationLayoutLunch.recyclerView.adapter =adapter
                            views[i].notificationLayoutLunch.recyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                            Log.d("StagesAfterAdd", itemList.toString())

                        }
                        "Dinner" ->
                        {
                            views[i].notificationLayoutDinner.recyclerView.adapter =adapter
                            views[i].notificationLayoutDinner.recyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                            Log.d("StagesAfterAdd", itemList.toString())


                        }

                    }

                }

                     }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewsTexts=originalviewsTexts
        var counter :Int=0
        for (i in resources.getStringArray(R.array.week)) {
            Daysvalue[i] = counter +1
            counter +=1
        }
        counter =0

        _binding = FragmentPlannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        views = listOf(binding.menuLayout0,
            binding.menuLayout1, binding.menuLayout2,
            binding.menuLayout3,binding.menuLayout4,binding.menuLayout5,binding.menuLayout6)
        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val day=sharedPref.getString("startDay","Sunday")
        val startDay =Daysvalue[day]
        if (day != "Sunday")
        {
            viewsTexts =originalviewsTexts.drop(startDay!!.minus(1)) +originalviewsTexts.take(startDay -1)
        }
        calendar= Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK,startDay!!)
        startDate = calendar.time
        menusetup()
        calendar.add(Calendar.DAY_OF_WEEK, -1)
        endDate = calendar.time
        calendar.clear()
        binding.menuLayout6.weeklyCalendarValues.setBackgroundDrawable(null)
        binding.weekRangeTextView.text = " ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)} "
        val rippleDrawable = RippleDrawable(ColorStateList.valueOf(Color.parseColor("#757575")),
            null, null)
        binding.previousButtonLayout.setOnClickListener {
            calendar.time =startDate
            calendar.add(Calendar.DAY_OF_WEEK, -7)
            startDate = calendar.time
            datevalue.clear()
            menusetup()
            calendar.add(Calendar.DAY_OF_WEEK, -1)
            endDate = calendar.time
            binding.weekRangeTextView.text = " ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)} "

        }
         binding.nextButtonLayout.setOnClickListener {
            calendar.time=endDate
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            startDate = calendar.time
            datevalue.clear()
            menusetup()
            calendar.add(Calendar.DAY_OF_WEEK, -1)
            endDate = calendar.time
            binding.weekRangeTextView.text = " ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)} "
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun menusetup()
    {
        datevalue.clear()
        for (i in views.indices) {
            nextDate = calendar.time
            Log.d("CalendarStartDateInside",nextDate.toString())
            datevalue.add(dateFormat.format(nextDate))
            views[i].notificationLayout.Add.setTag(viewsTexts[i]+","+datevalue[i]+",Breakfast")
            views[i].notificationLayout.Add.setOnClickListener(myOnClickListener)
            views[i].notificationLayoutLunch.Add.setTag(viewsTexts[i]+","+datevalue[i]+",Lunch")
            views[i].notificationLayoutLunch.Add.setOnClickListener(myOnClickListener)
            views[i].notificationLayoutDinner.Add.setTag(viewsTexts[i]+","+datevalue[i]+",Dinner")
            views[i].notificationLayoutDinner.Add.setOnClickListener(myOnClickListener)
            views[i].Day.text = viewsTexts[i]
            views[i].Date.text =dateOnlyFormat.format(nextDate)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            loadingRecyclerViews(i)

        }

    }
    val myOnClickListener = View.OnClickListener {
        tagdata=it.getTag() as String
        val customDialog = AddItemsDialog(requireContext(),viewModel,tagdata,lifecycleScope)
        customDialog.setOnItemSelectedListener(this@PlannerFragment)
        customDialog.show()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_share -> {
                        val rootView =binding.startLayout
                        rootView?.let {
                            val screenshot =
                                Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(screenshot)
                            it.draw(canvas)
                            var cachePath = File(context?.getCacheDir(), "Pictures")
                            cachePath.mkdir()
                            val file = File(cachePath, "screenshot.png")
                            val fileOutputStream = FileOutputStream(file)
                            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                            fileOutputStream.close()
                            // Now 'screenshot' contains the captured image of your fragment

                            val uri = FileProvider.getUriForFile(requireContext(),
                                "com.example.mymealmenu.fileprovider",
                                file)
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "image/png"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(Intent.createChooser(shareIntent, "Share Menu"))
                        }


// The screenshot is saved to 'file' on the device's storage


                        true
                    }

                    R.id.action_delete -> {
                        lifecycleScope.launch {
                            for (i in views.indices) {
                                viewModel.deletemenusthisweek(datevalue[i])
                                loadingRecyclerViews(i)
                            }
                        }
                        true
                    }

                    R.id.action_saveastemplate -> {
                        val customAddDishesDialog = AddDishesDialog(requireContext(),viewModel,null,null,null,null)
                        customAddDishesDialog.setOnItemSelectedListener(this@PlannerFragment)
                        customAddDishesDialog.show()

                        true
                    }

                    R.id.action_addtemplate -> {
                        val customAdd4mTemplateDialog = Add4mTemplateDialog(requireContext(),viewModel)
                        customAdd4mTemplateDialog.setOnItemSelectedListener(this@PlannerFragment)
                        customAdd4mTemplateDialog.show()
                        true
                    }
                    R.id.action_exit -> {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Exit Application")
                        // Create a TextView to set the text color
                        val message = TextView(requireContext())
                        message.text = "\n         Are you sure you want to exit?"
                        message.setTextColor(ContextCompat.getColor(requireContext(),
                            R.color.purple_200))
                        builder.setView(message)
                        builder.setPositiveButton("Yes") { dialog, _ ->
                            // code to exit the application
                            requireActivity().finishAffinity()
                        }
                        builder.setNegativeButton("No") { dialog, _ ->
                            // code to handle canceling the exit
                            dialog.dismiss()
                        }
                        val dialog = builder.create()
                        dialog.show()
                        true
                    }
                    else -> true
                }

                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun loadingRecyclerViews(i :Int)
    {
        lifecycleScope.launch {
            val itemListBreakfast = viewModel.getMenuonDateMealType(datevalue[i], "Breakfast")
            adapter = MenuItemAdapter(requireContext(),itemListBreakfast, "display",viewModel)
            views[i].notificationLayout.recyclerView.adapter = adapter
            views[i].notificationLayout.recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            adapter!!.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    val customEditItemsDialog = EditItemsDialog(requireContext(),
                        viewModel,item,"FromMenu",datevalue[i],"Breakfast")
                    customEditItemsDialog.setOnEditItemSelectedListener(this@PlannerFragment)
                    customEditItemsDialog.show()
                }
            })
            val itemListLunch = viewModel.getMenuonDateMealType(datevalue[i], "Lunch")
            adapter = MenuItemAdapter(requireContext(),itemListLunch, "display",viewModel)
            views[i].notificationLayoutLunch.recyclerView.adapter = adapter
            views[i].notificationLayoutLunch.recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            adapter!!.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    val customEditItemsDialog = EditItemsDialog(requireContext(),
                        viewModel,item,"FromMenu",datevalue[i],"Lunch")
                    customEditItemsDialog.setOnEditItemSelectedListener(this@PlannerFragment)
                    customEditItemsDialog.show()
                }
            })
            val itemListDinner = viewModel.getMenuonDateMealType(datevalue[i], "Dinner")
            adapter = MenuItemAdapter(requireContext(),itemListDinner, "display",viewModel)
            views[i].notificationLayoutDinner.recyclerView.adapter = adapter
            views[i].notificationLayoutDinner.recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            adapter!!.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    val customEditItemsDialog = EditItemsDialog(requireContext(),
                        viewModel,item,"FromMenu",datevalue[i],"Dinner")
                    customEditItemsDialog.setOnEditItemSelectedListener(this@PlannerFragment)
                    customEditItemsDialog.show()
                }
            })
        }
    }
}