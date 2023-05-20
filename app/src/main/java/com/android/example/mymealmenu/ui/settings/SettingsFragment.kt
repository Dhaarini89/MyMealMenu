package com.android.example.mymealmenu.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.example.mymealmenu.MainActivity.Companion.Daysvalue
import com.android.example.mymealmenu.ui.notification.PlannerNotificationWorker
import com.android.example.mymealmenu.R

import com.android.example.mymealmenu.databinding.FragmentSettingsBinding
import com.android.example.mymealmenu.ui.notification.MenuNotificationWorker

import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {
    var selectedDay: String? = null
    var list_of_items = arrayOf("Item 1", "Item 2", "Item 3")
    private lateinit var sharedPref: SharedPreferences
    private var _binding: FragmentSettingsBinding? = null
    private val viewModel: GenericViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var timeArray = emptyArray<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        timeArray +="Never"
        for (i in 0..9) {
            timeArray += "0${i}:00"
        }
        for (i in 10..23) {
            timeArray += "${i}:00"
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.notifyMenuTime.adapter = adapter
        binding.notifyPlanDays.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedPlanDay = parent.getItemAtPosition(position) as String
                    val editor = sharedPref.edit()
                    editor.putString("selectPlanDay", selectedPlanDay)
                    editor.apply()
                    val planmealcheck = sharedPref.getBoolean("Plannernotify_switch_state", false)
                    if (planmealcheck == true) {
                        setNotification()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        val position = resources.getStringArray(R.array.week)
            .indexOf(sharedPref.getString("StartDay", "Sunday"))
        binding.days.setSelection(position)
        binding.days.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDay = parent.getItemAtPosition(position) as String
                Log.d("StartDay", selectedDay.toString())
                val editor = sharedPref.edit()
                editor.putString("startDay", selectedDay)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.notifyMenuTime.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                var isFirstSelection = true
                var previousSelectedItem: String? = null
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedMenuTime = parent.getItemAtPosition(position) as String
                    val editor = sharedPref.edit()
                    editor.putString("selectedMenuTime", selectedMenuTime)
                    editor.apply()
                    if (isFirstSelection) {
                        // Skip performing actions on the initial selection
                        isFirstSelection = false
                        previousSelectedItem = selectedMenuTime
                        return
                    }
                    if (selectedMenuTime != previousSelectedItem) {
                        val mealcheck = sharedPref.getBoolean("notifyMenu_switch_state", false)
                        if (mealcheck == true) {
                            setNotification()
                            Log.d("MenuNotificationinsideonItemSelected", mealcheck.toString())

                        }
                        previousSelectedItem = selectedMenuTime
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        //  WorkManager.getInstance(requireContext()).cancelUniqueWork("MenuNotification_Work")
        //    WorkManager.getInstance(requireContext()).cancelUniqueWork("PlannerNotification_Work")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    fun setNotification() {
        setPlannerNotification()
        setMenuNotification()
    }

    fun setPlannerNotification() {

        val selectedPlanDay = sharedPref.getString("selectedPlanDay", "Sunday")
        val startTime = LocalDateTime.now()
            .with(DayOfWeek.valueOf(selectedPlanDay!!.uppercase()))
            .withHour(10)
            .withMinute(0)
            .withSecond(0)
        // val selectedMenuTime=sharedPref.getString("selectedMenuTime","8:00")!!.split(":")[0].toInt()*60*60*1000
        val currentTime = LocalDateTime.now()
        val delay = Duration.between(currentTime, startTime)
        val periodicRequest: PeriodicWorkRequest
        periodicRequest = PeriodicWorkRequestBuilder<MenuNotificationWorker>(1, TimeUnit.MINUTES)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("MenuNotification_Work",
            ExistingPeriodicWorkPolicy.REPLACE, periodicRequest)

        //  WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("PlannerNotification_Work",
        //    ExistingPeriodicWorkPolicy.REPLACE,periodicRequest)
    }
    fun setMenuNotification() {
        val startTime = LocalDateTime.now()
            .withHour(17)
            .withMinute(0)
            .withSecond(0)
        // val selectedMenuTime=sharedPref.getString("selectedMenuTime","8:00")!!.split(":")[0].toInt()*60*60*1000
        val currentTime = LocalDateTime.now()
        val initialDelay = if (currentTime.isAfter(startTime)) {
            // Calculate the delay until the next occurrence of the specified start time
            val nextStartTime = startTime.plusMinutes(12)
            Duration.between(currentTime, nextStartTime).toMillis()
        } else {
            // Calculate the delay until the specified start time
            Duration.between(currentTime, startTime).toMillis()
        }
        val periodicRequest: PeriodicWorkRequest
        periodicRequest = PeriodicWorkRequestBuilder<MenuNotificationWorker>(1, TimeUnit.MINUTES)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("MenuNotification_Work",
            ExistingPeriodicWorkPolicy.REPLACE, periodicRequest)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}