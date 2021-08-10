package br.com.pignata.tuhm.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pignata.tuhm.data.database.entity.ProjectEntity
import br.com.pignata.tuhm.databinding.FragmentListProjectsBinding
import br.com.pignata.tuhm.view.adapter.ListProjectsAdapter
import br.com.pignata.tuhm.view.view.setOnSingleClickListener
import br.com.pignata.tuhm.view.view_model.ListProjectsViewModel
import br.com.pignata.tuhm.view.view_model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListProjectsFragment : Fragment() {
    private lateinit var binding: FragmentListProjectsBinding
    private val listProjectsViewModel: ListProjectsViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val navController: NavController by lazy { findNavController() }
    private val listProjectsAdapter: ListProjectsAdapter by lazy { ListProjectsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listProjectsViewModel.listProjects.observe(viewLifecycleOwner) {
            when (it.state) {
                ListProjectsViewModel.ListProjectsState.STATE_EMPTY -> stateEmpty()
                ListProjectsViewModel.ListProjectsState.STATE_LIST -> stateList(it.data)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listProjectsAdapter
        }

        binding.fabAdd.setOnSingleClickListener {
            navController.navigate(ListProjectsFragmentDirections.actionListProjectsFragmentToNewProjectFragment())
        }

        listProjectsAdapter.setOnClick { projectEntity, _ ->
            navController.navigate(
                ListProjectsFragmentDirections.actionListProjectsFragmentToProjectFragment(
                    projectEntity.id
                )
            )
        }

        mainViewModel.textSearch.observe(viewLifecycleOwner) { search(it) }

        listProjectsViewModel.loadAllProjects()
    }

    private fun search(query: String?) {
        if (query?.isNotEmpty() == true) {
            val list = listProjectsAdapter.list.filter {
                it.title?.contains(query, true) == true ||
                        it.description?.contains(query, true) == true
            }
            listProjectsAdapter.updateSearchList(list)
        } else {
            listProjectsAdapter.updateListForDefault()
        }
    }

    private fun stateEmpty() {
        binding.recyclerView.visibility = View.GONE
        binding.txtListEmpty.visibility = View.VISIBLE
    }

    private fun stateList(list: List<ProjectEntity>) {
        binding.txtListEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        listProjectsAdapter.updateList(list)
    }
}