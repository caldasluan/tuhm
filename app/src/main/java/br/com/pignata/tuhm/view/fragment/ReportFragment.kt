package br.com.pignata.tuhm.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pignata.tuhm.BuildConfig
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.data.database.entity.ProblemEntity
import br.com.pignata.tuhm.databinding.FragmentReportBinding
import br.com.pignata.tuhm.view.adapter.ReportAdapter
import br.com.pignata.tuhm.view.view_model.ReportViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.math.max


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private val args: ReportFragmentArgs by navArgs()
    private val reportAdapter: ReportAdapter by lazy { ReportAdapter() }
    private val navController: NavController by lazy { findNavController() }
    private val reportViewModel: ReportViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val project = args.projectWithProblem.project
        val problems = args.projectWithProblem.listProblems.sortedByDescending { it.gravity }

        binding.txtTitle.text = project.title
        binding.txtDescription.text = project.description
        binding.txtProblemsFound.text = problems.size.toString()
        binding.txtViolatedHeuristics.text = problems.fold(0) { count, problem ->
            count + (problem.listHeuristics?.size ?: 0)
        }.toString()

        updateHeightGraphic(problems)

        binding.txtGravityAbsent.text =
            getString(R.string.txt_gravity_absent, problems.count { it.gravity == 0 })
        binding.txtGravityCosmetic.text =
            getString(R.string.txt_gravity_cosmetic, problems.count { it.gravity == 1 })
        binding.txtGravitySmall.text =
            getString(R.string.txt_gravity_small, problems.count { it.gravity == 2 })
        binding.txtGravityGreat.text =
            getString(R.string.txt_gravity_great, problems.count { it.gravity == 3 })
        binding.txtGravityCatastrofic.text =
            getString(R.string.txt_gravity_catastrofic, problems.count { it.gravity == 4 })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }

        reportAdapter.updateList(problems)

        reportAdapter.setOnClick { problemEntity, _ ->
            navController.navigate(
                ReportFragmentDirections.actionReportFragmentToReportProblemFragment(
                    problemEntity
                )
            )
        }

        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_share)
        item.setOnMenuItemClickListener {
            Snackbar.make(binding.root, getString(R.string.txt_sharing), Snackbar.LENGTH_LONG)
                .show()

            val file = generatePDF()
            val uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, file)

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            startActivity(intent)

            true
        }
    }

    private fun updateHeightGraphic(problems: List<ProblemEntity>) {
        val density = context?.resources?.displayMetrics?.density ?: 1F
        binding.imgGravityAbsent.updateLayoutParams {
            height =
                calculateHeightGraphic(problems.count { it.gravity == 0 }, problems.size, density)
        }
        binding.imgGravityCosmetic.updateLayoutParams {
            height =
                calculateHeightGraphic(problems.count { it.gravity == 1 }, problems.size, density)
        }
        binding.imgGravitySmall.updateLayoutParams {
            height =
                calculateHeightGraphic(problems.count { it.gravity == 2 }, problems.size, density)
        }
        binding.imgGravityGreat.updateLayoutParams {
            height =
                calculateHeightGraphic(problems.count { it.gravity == 3 }, problems.size, density)
        }
        binding.imgGravityCatastrofic.updateLayoutParams {
            height =
                calculateHeightGraphic(problems.count { it.gravity == 4 }, problems.size, density)
        }
    }

    private fun calculateHeightGraphic(count: Int, total: Int, density: Float): Int =
        max(((count.toFloat() / total) * 80F * density).toInt(), 1)

    private fun generatePDF(): File {
        val fileDir = File(context?.filesDir, File.separator + "reports")
        if (!fileDir.exists()) fileDir.mkdir()

        val file = File(fileDir, File.separator + "report.pdf")
        if (!file.exists()) file.createNewFile()

        reportViewModel.generateReportPdf(
            context,
            file,
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_launcher_foreground)
                ?.apply {
                    setTint(
                        ResourcesCompat.getColor(
                            requireContext().resources,
                            R.color.green,
                            null
                        )
                    )
                }?.toBitmap(width = 120, height = 120),
            binding.layoutGraphic.drawToBitmap(),
            args.projectWithProblem
        )

        return file
    }
}