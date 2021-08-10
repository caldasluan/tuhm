package br.com.pignata.tuhm.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.databinding.FragmentProjectBinding
import br.com.pignata.tuhm.view.adapter.ProjectAdapter
import br.com.pignata.tuhm.view.view.setOnSingleClickListener
import br.com.pignata.tuhm.view.view_model.MainViewModel
import br.com.pignata.tuhm.view.view_model.ProjectViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectFragment : Fragment() {
    private lateinit var binding: FragmentProjectBinding
    private val projectViewModel: ProjectViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val args: ProjectFragmentArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }
    private val projectAdapter by lazy { ProjectAdapter(context) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectViewModel.project.observe(viewLifecycleOwner) {
            binding.txtTitle.text = it.data?.project?.title
            binding.txtDescription.text = it.data?.project?.description

            when (it.state) {
                ProjectViewModel.ProjectState.STATE_EMPTY -> stateEmpty()
                ProjectViewModel.ProjectState.STATE_LIST -> stateList(it.data?.listProblems)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = projectAdapter
        }

        projectAdapter.setOnClick { problemEntity, _ ->
            mainViewModel.clearHeuristic()
            mainViewModel.addAllCheckHeuristic(problemEntity.listHeuristics)
            navController.navigate(
                ProjectFragmentDirections.actionProjectFragmentToNewProblemFragment(
                    args.idProject,
                    problemEntity,
                    true
                )
            )
        }

        binding.btnEdit.setOnSingleClickListener {
            navController.navigate(
                ProjectFragmentDirections.actionProjectFragmentToNewProjectFragment(
                    projectViewModel.project.value?.data?.project,
                    true
                )
            )
        }

        binding.btnReport.setOnSingleClickListener {
            projectViewModel.project.value?.data?.let {
                navController.navigate(
                    ProjectFragmentDirections.actionProjectFragmentToReportFragment(
                        it
                    )
                )
            }
        }

        binding.fabAdd.setOnSingleClickListener {
            mainViewModel.clearHeuristic()
            navController.navigate(
                ProjectFragmentDirections.actionProjectFragmentToNewProblemFragment(
                    args.idProject
                )
            )
        }

        mainViewModel.textSearch.observe(viewLifecycleOwner, this::search)

        projectViewModel.loadProject(args.idProject)
    }

    private fun search(query: String?) {
        if (query?.isNotEmpty() == true) {
            val list = projectAdapter.list.filter {
                it.description?.contains(query, true) == true
            }
            projectAdapter.updateSearchList(list)
        } else {
            projectAdapter.updateListForDefault()
        }
    }

    private fun stateEmpty() {
        binding.txtListEmpty.visibility = View.VISIBLE
    }

    private fun stateList(list: List<ProblemEntity>?) {
        binding.txtListEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        projectAdapter.updateList(list)
    }
}