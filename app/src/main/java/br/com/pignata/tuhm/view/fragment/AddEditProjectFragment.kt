package br.com.pignata.tuhm.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.FragmentAddEditProjectBinding
import br.com.pignata.tuhm.view.view.setOnSingleClickListener
import br.com.pignata.tuhm.view.view_model.AddEditProjectViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class AddEditProjectFragment : Fragment() {
    private lateinit var binding: FragmentAddEditProjectBinding
    private val viewModel: AddEditProjectViewModel by stateViewModel()
    private val navController: NavController by lazy { findNavController() }
    private val args: AddEditProjectFragmentArgs by navArgs()
    private val mode by lazy { if (args.project != null) ModeProject.MODE_EDIT else ModeProject.MODE_ADD }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.addEditProjectState.observe(viewLifecycleOwner) {
            when (it) {
                AddEditProjectViewModel.AddEditProjectStatus.STATE_WAIT -> stateWait()
                AddEditProjectViewModel.AddEditProjectStatus.STATE_LOADING -> stateLoading()
                AddEditProjectViewModel.AddEditProjectStatus.STATE_ADD -> stateAdd()
                AddEditProjectViewModel.AddEditProjectStatus.STATE_EDIT -> stateEdit()
                AddEditProjectViewModel.AddEditProjectStatus.STATE_DELETE -> stateDelete()
                else -> stateWait()
            }
        }

        binding.title.editText?.doOnTextChanged { _, _, _, _ -> binding.title.isErrorEnabled = false }
        binding.description.editText?.doOnTextChanged { _, _, _, _ ->
            binding.title.isErrorEnabled = false
        }

        binding.btnSave.setOnSingleClickListener {
            binding.title.error = if (binding.title.editText?.text.isNullOrEmpty())
                getString(R.string.error_title_project_null) else null
            binding.description.error = if (binding.description.editText?.text.isNullOrEmpty())
                getString(R.string.error_description_project_null) else null

            if (binding.title.error.isNullOrEmpty() && binding.description.error.isNullOrEmpty()) {
                if (mode == ModeProject.MODE_EDIT) {
                    viewModel.updateProject(
                        args.project?.id ?: 0,
                        binding.title.editText?.text.toString(),
                        binding.description.editText?.text.toString()
                    )
                } else {
                    viewModel.insertProject(
                        binding.title.editText?.text.toString(),
                        binding.description.editText?.text.toString()
                    )
                }
            }
        }
        binding.btnCancel.setOnSingleClickListener { navController.navigateUp() }

        if (mode == ModeProject.MODE_EDIT) {
            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.title_edit_project)
            binding.title.editText?.setText(args.project?.title)
            binding.description.editText?.setText(args.project?.description)
            setHasOptionsMenu(true)
        }

        viewModel.stateToolbarTitle.observe(viewLifecycleOwner) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = it
        }
        viewModel.stateTitle.observe(viewLifecycleOwner) {
            binding.title.editText?.setText(it)
        }
        viewModel.stateDescription.observe(viewLifecycleOwner) {
            binding.description.editText?.setText(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(
            (activity as? AppCompatActivity)?.supportActionBar?.title.toString(),
            binding.title.editText?.text.toString(),
            binding.description.editText?.text.toString()
        )
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_delete)
        item.setOnMenuItemClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.title_alert_delete_project)
                .setMessage(R.string.txt_alert_delete_project)
                .setPositiveButton(R.string.btn_delete) { _, _ ->
                    args.project?.let {
                        viewModel.deleteProject(it)
                    }
                }
                .setNegativeButton(R.string.btn_cancel) { dialog, _ -> dialog.dismiss() }
                .show()
            true
        }
    }

    private fun stateWait() {
        binding.progressHorizontal.visibility = View.GONE
    }

    private fun stateLoading() {
        binding.progressHorizontal.visibility = View.VISIBLE
    }

    private fun stateAdd() {
        Snackbar.make(binding.root, R.string.txt_create_project, Snackbar.LENGTH_LONG).show()
        navController.navigateUp()
    }

    private fun stateEdit() {
        Snackbar.make(binding.root, R.string.txt_edit_project, Snackbar.LENGTH_LONG).show()
        navController.navigateUp()
    }

    private fun stateDelete() {
        Snackbar.make(binding.root, R.string.txt_delete_project, Snackbar.LENGTH_LONG).show()
        navController.popBackStack(R.id.listProjectsFragment, false)
    }

    private enum class ModeProject {
        MODE_ADD, MODE_EDIT
    }
}