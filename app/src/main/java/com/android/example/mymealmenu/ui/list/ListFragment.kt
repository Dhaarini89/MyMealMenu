package com.android.example.mymealmenu.ui.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.mymealmenu.R
import com.android.example.mymealmenu.databinding.FragmentListBinding
import com.android.example.mymealmenu.ui.adapter.MenuItemAdapter
import com.android.example.mymealmenu.ui.customdialog.AddDishesDialog
import com.android.example.mymealmenu.ui.customdialog.EditItemsDialog
import com.android.example.mymealmenu.ui.viewmodel.GenericViewModel
import com.android.example.mymealmenu.ui.database.DishesList
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment(),EditItemsDialog.OnEditItemSelectedListener,AddDishesDialog.OnTemplateSelectedListener {

    private var _binding: FragmentListBinding? = null
    private val viewModel : GenericViewModel by viewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var items :List<DishesList> =listOf()
    override fun onEditItemSelected(status: String) {
          loadingRecyclerView()

    }

    override fun onTemplateSelected(template: String) {
        loadingRecyclerView()

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadingRecyclerView()
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Add.setOnClickListener {
            val customDialog = AddDishesDialog(requireContext(),viewModel,binding,null,null,null)
            customDialog.setOnItemSelectedListener(this@ListFragment)
            customDialog.show()
        }
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_disheslistlayout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {

                    R.id.action_deletelist -> {
                        lifecycleScope.launch {
                            viewModel.deleteDishes()
                            loadingRecyclerView()
                            true
                        }

                    }
                    else -> true
                }
                    return true

            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        }

    fun loadingRecyclerView()
    {
        lifecycleScope.launch {
            items = withContext(Dispatchers.IO) {
                viewModel.getDishes()
            }
            val itemsList =items.map(DishesList::itemName)
            val adapter =MenuItemAdapter(requireContext(),itemsList, "add",viewModel)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter.setOnItemClickListener(object : MenuItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    loadingRecyclerView()
                }
            })
        }
    }
}