package br.com.pignata.tuhm.view.activity

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import br.com.pignata.tuhm.R
import br.com.pignata.tuhm.databinding.ActivityMainBinding
import br.com.pignata.tuhm.view.view_model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy { findNavController(R.id.fragment) }
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val menuItem = setupSearch(menu)

        navController.addOnDestinationChangedListener { _, _, arguments ->
            menuItem?.collapseActionView()

            binding.toolbar.menu.findItem(R.id.action_search).isVisible =
                arguments?.getBoolean("MenuSearch", false) == true
            binding.toolbar.menu.findItem(R.id.action_share).isVisible =
                arguments?.getBoolean("MenuShare", false) == true
            binding.toolbar.menu.findItem(R.id.action_delete).isVisible =
                arguments?.getBoolean("MenuDelete", false) == true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupSearch(menu: Menu): MenuItem? {
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as? SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.maxWidth = Int.MAX_VALUE
        searchView?.queryHint = resources.getString(R.string.txt_search)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = querySearch(query)
            override fun onQueryTextChange(newText: String?): Boolean = querySearch(newText)
        })

        val searchEditText: EditText? = searchView?.findViewById(R.id.search_src_text)
        searchEditText?.let {
            it.setTextColor(ResourcesCompat.getColor(resources, R.color.white, theme))
            it.setHintTextColor(ResourcesCompat.getColor(resources, R.color.white, theme))
        }

        return menuItem
    }

    private fun querySearch(query: String?): Boolean {
        mainViewModel.udpateTextSearch(query)
        return false
    }
}