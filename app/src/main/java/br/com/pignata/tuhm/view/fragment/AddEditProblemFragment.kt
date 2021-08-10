package br.com.pignata.tuhm.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.FragmentAddEditProblemBinding
import br.com.pignata.tuhm.view.adapter.NewProblemAdapter
import br.com.pignata.tuhm.view.view.setOnSingleClickListener
import br.com.pignata.tuhm.view.view_model.AddEditProblemViewModel
import br.com.pignata.tuhm.view.view_model.MainViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class AddEditProblemFragment : Fragment() {
    private lateinit var binding: FragmentAddEditProblemBinding
    private val addEditProblemViewModel: AddEditProblemViewModel by stateViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val navController: NavController by lazy { findNavController() }
    private val args: AddEditProblemFragmentArgs by navArgs()
    private val newProblemAdapter by lazy { NewProblemAdapter() }
    private val listHeuristic by lazy { context?.resources?.getStringArray(R.array.list_title_heuristic) }
    private val listGravity by lazy { context?.resources?.getStringArray(R.array.list_gravity) }
    private val mode by lazy { if (args.problem != null) ModeProblem.MODE_EDIT else ModeProblem.MODE_ADD }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditProblemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtDescription.editText?.doOnTextChanged { _, _, _, _ ->
            binding.edtDescription.error = null
        }
        binding.edtGravity.editText?.doOnTextChanged { _, _, _, _ ->
            binding.edtGravity.error = null
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newProblemAdapter
        }

        binding.btnAdd.setOnSingleClickListener {
            navController.navigate(
                AddEditProblemFragmentDirections.actionNewProblemFragmentToListHeuristicsFragment()
            )
        }

        binding.btnSave.setOnSingleClickListener {
            binding.edtGravity.error = if (binding.edtGravity.editText?.text.isNullOrEmpty())
                getString(R.string.error_gravity_problem_null) else null

            if (binding.edtGravity.error.isNullOrEmpty()) {
                val listHeuristics = mutableListOf<Int>()
                mainViewModel.heuristicsSelected.value?.forEach {
                    if (it.value) listHeuristics.add(it.key)
                }
                if (mode == ModeProblem.MODE_EDIT) {
                    addEditProblemViewModel.updateProblem(
                        args.problem?.id ?: 0,
                        binding.edtDescription.editText?.text.toString(),
                        listGravity?.indexOf(binding.edtGravity.editText?.text.toString()) ?: 0,
                        listHeuristics,
                        args.idContrato
                    )
                } else {
                    addEditProblemViewModel.insertProblem(
                        binding.edtDescription.editText?.text.toString(),
                        listGravity?.indexOf(binding.edtGravity.editText?.text.toString()) ?: 0,
                        listHeuristics,
                        args.idContrato
                    )
                }
            }
        }

        binding.btnCancel.setOnSingleClickListener { navController.popBackStack() }

        if (mode == ModeProblem.MODE_EDIT) {
            binding.edtDescription.editText?.setText(args.problem?.description)
            binding.edtGravity.editText?.setText(listGravity?.get(args.problem?.gravity ?: 0))
            setHasOptionsMenu(true)
        }

        setArrayAdapter()

        mainViewModel.heuristicsSelected.observe(viewLifecycleOwner) {
            val list = mutableListOf<String>()
            it.toSortedMap().forEach { map ->
                if (map.value) {
                    listHeuristic?.let { itList -> list.add(itList[map.key]) }
                }
            }
            if (list.isEmpty()) list.add(getString(R.string.error_list_heuristics_empty))
            newProblemAdapter.updateList(list)
        }

        addEditProblemViewModel.newProblemState.observe(viewLifecycleOwner) {
            when (it) {
                AddEditProblemViewModel.NewProblemStatus.STATE_WAIT -> stateWait()
                AddEditProblemViewModel.NewProblemStatus.STATE_LOADING -> stateLoading()
                AddEditProblemViewModel.NewProblemStatus.STATE_ADD -> stateAdd()
                AddEditProblemViewModel.NewProblemStatus.STATE_EDIT -> stateEdit()
                AddEditProblemViewModel.NewProblemStatus.STATE_DELETE -> stateDelete()
                else -> stateWait()
            }
        }

        addEditProblemViewModel.stateDescription.observe(viewLifecycleOwner) {
            binding.edtDescription.editText?.setText(it)
        }
        addEditProblemViewModel.stateGravity.observe(viewLifecycleOwner) {
            binding.edtGravity.editText?.setText(it)
            setArrayAdapter()
        }
    }

    override fun onPause() {
        super.onPause()
        saveInstance()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstance()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_delete)
        item.setOnMenuItemClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.title_alert_delete_problem)
                .setMessage(R.string.txt_alert_delete_problem)
                .setPositiveButton(R.string.btn_delete) { _, _ ->
                    args.problem?.let {
                        addEditProblemViewModel.deleteProblem(it)
                    }
                }
                .setNegativeButton(R.string.btn_cancel) { dialog, _ -> dialog.dismiss() }
                .show()
            true
        }
    }

    private fun setArrayAdapter() {
        (binding.edtGravity.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.list_item,
                listGravity ?: arrayOf<String>()
            )
        )
    }

    private fun saveInstance() {
        if (this::binding.isInitialized) {
            addEditProblemViewModel.saveState(
                binding.edtDescription.editText?.text.toString(),
                binding.edtGravity.editText?.text.toString()
            )
        }
    }

    private fun stateWait() {
        binding.progressHorizontal.visibility = View.GONE
    }

    private fun stateLoading() {
        binding.progressHorizontal.visibility = View.VISIBLE
    }

    private fun stateAdd() {
        Snackbar.make(binding.root, R.string.txt_create_problem, Snackbar.LENGTH_LONG).show()
        navController.navigateUp()
    }

    private fun stateEdit() {
        Snackbar.make(binding.root, R.string.txt_edit_problem, Snackbar.LENGTH_LONG).show()
        navController.navigateUp()
    }

    private fun stateDelete() {
        Snackbar.make(binding.root, R.string.txt_delete_problem, Snackbar.LENGTH_LONG).show()
        navController.navigateUp()
    }

    private enum class ModeProblem {
        MODE_ADD, MODE_EDIT
    }
}