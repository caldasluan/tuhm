package br.com.pignata.tuhm.view.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
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
import java.io.File
import java.io.FileOutputStream


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
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val imageStream = context?.contentResolver?.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            addEditProblemViewModel.saveImage(bitmap)
        } ?: run {
            addEditProblemViewModel.saveImage(null)
        }
    }

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
            binding.edtDescription.isErrorEnabled = false
        }
        binding.edtGravity.editText?.doOnTextChanged { _, _, _, _ ->
            binding.edtGravity.isErrorEnabled = false
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

        binding.btnSave.setOnSingleClickListener { clickSave() }

        binding.btnCancel.setOnSingleClickListener { navController.popBackStack() }

        binding.btnEditImg.setOnSingleClickListener {
            addEditProblemViewModel.image.value?.let {
                AlertDialog.Builder(context)
                    .setTitle(R.string.title_alert_change_img_problem)
                    .setMessage(R.string.txt_alert_change_img_problem)
                    .setPositiveButton(R.string.btn_change) { _, _ -> getImage.launch("image/*") }
                    .setNegativeButton(R.string.btn_remove) { _, _ ->
                        addEditProblemViewModel.saveImage(null)
                    }
                    .show()
            } ?: run { getImage.launch("image/*") }
        }

        if (mode == ModeProblem.MODE_EDIT) {
            binding.edtDescription.editText?.setText(args.problem?.description)
            binding.edtGravity.editText?.setText(listGravity?.get(args.problem?.gravity ?: 0))
            addEditProblemViewModel.saveImageInitialize(BitmapFactory.decodeFile(args.problem?.srcImage))
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
        addEditProblemViewModel.image.observe(viewLifecycleOwner) {
            it?.let { binding.imgProblem.setImageBitmap(it) } ?: run {
                binding.imgProblem.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_empty_img)
                )
            }
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

    private fun clickSave() {
        binding.edtGravity.error = if (binding.edtGravity.editText?.text.isNullOrEmpty())
            getString(R.string.error_gravity_problem_null) else null
        binding.edtDescription.error = if (binding.edtDescription.editText?.text.isNullOrEmpty())
            getString(R.string.error_description_problem_null) else null

        if (binding.edtGravity.error.isNullOrEmpty() && binding.edtDescription.error.isNullOrEmpty()) {
            val listHeuristics = mutableListOf<Int>()
            mainViewModel.heuristicsSelected.value?.forEach {
                if (it.value) listHeuristics.add(it.key)
            }
            val path = updateImage()
            if (mode == ModeProblem.MODE_EDIT) {
                addEditProblemViewModel.updateProblem(
                    args.problem?.id ?: 0,
                    binding.edtDescription.editText?.text.toString(),
                    listGravity?.indexOf(binding.edtGravity.editText?.text.toString()) ?: 0,
                    listHeuristics,
                    path,
                    args.idContrato
                )
            } else {
                addEditProblemViewModel.insertProblem(
                    binding.edtDescription.editText?.text.toString(),
                    listGravity?.indexOf(binding.edtGravity.editText?.text.toString()) ?: 0,
                    listHeuristics,
                    path,
                    args.idContrato
                )
            }
        }
    }

    private fun updateImage(): String? {
        try {
            val file = if (args.problem?.srcImage.isNullOrEmpty()) {
                val fileName = "problem_${args.idContrato}_${System.currentTimeMillis()}.jpg"

                val fileDir = File(context?.filesDir, File.separator + "img_problems")
                if (!fileDir.exists()) fileDir.mkdir()

                File(fileDir, File.separator + fileName)
            } else {
                File(args.problem?.srcImage)
            }

            addEditProblemViewModel.image.value?.let {
                if (!file.exists()) file.createNewFile()

                val out = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                return file.path
            } ?: kotlin.run {
                if (file.exists()) file.delete()
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
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