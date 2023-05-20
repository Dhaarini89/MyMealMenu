package com.android.example.mymealmenu.ui.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.R
import com.android.example.mymealmenu.databinding.FragmentListBinding
import com.android.example.mymealmenu.databinding.FragmentTemplateBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.customdialog.AddDishesDialog
import com.android.example.mymealmenu.ui.customdialog.EditItemsDialog
import com.android.example.mymealmenu.ui.database.DishesList
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TemplateFragment : Fragment() {

    private var _binding: FragmentTemplateBinding? = null
    private val viewModel : GenericViewModel by viewModels()
    private val binding get() = _binding!!
    private var items :List<String> =listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTemplateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadingRecyclerView()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_templatelistlayout, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {

                    R.id.action_deletelist -> {
                        lifecycleScope.launch {
                            viewModel.deleteTemplates()
                            loadingRecyclerView()
                            true
                        }

                    }
                    else -> true
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return root
    }

    fun loadingRecyclerView()
    {
        lifecycleScope.launch {
            items = withContext(Dispatchers.IO) {
                viewModel.getTemplateNames()
            }
            val adapter = MenuItemAdapter(requireContext(),items, "addTemplate",viewModel)
            Log.d("Template",items.toString())
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
           adapter.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    Log.d("TemplateinsideFragment","Yes")
                    loadingRecyclerView()
                }
            })


        }
    }
}