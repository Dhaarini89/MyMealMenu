package com.android.example.mymealmenu.ui.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.example.mymealmenu.ui.list.ListFragment
import com.android.example.mymealmenu.ui.list.TemplateFragment
import com.android.example.mymealmenu.ui.planner.PlannerFragment

private const val NUM_TABS = 2


class ViewPagerAdapter(fragment :Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                Log.d("PositionSelected","0")
                ListFragment()
            }
            1 ->
            {
                Log.d("PositionSelected","1")
                TemplateFragment()
            }
            else -> ListFragment()
        }

    }
}