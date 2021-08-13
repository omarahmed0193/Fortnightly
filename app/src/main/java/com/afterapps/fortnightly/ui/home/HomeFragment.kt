package com.afterapps.fortnightly.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afterapps.fortnightly.R
import com.afterapps.fortnightly.databinding.FragmentHomeBinding
import com.afterapps.fortnightly.util.DividerItemDecoration
import com.afterapps.fortnightly.util.Resource
import com.afterapps.fortnightly.util.observeInLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), TabLayout.OnTabSelectedListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var statusSnackbar: Snackbar
    private val currentTabPositionKey = "tabPosition"
    private var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { tabPosition = it.getInt(currentTabPositionKey, 0) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(currentTabPositionKey, tabPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        initViews(binding)

        initObservers()
    }

    private fun initViews(binding: FragmentHomeBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val tabLayout = binding.articleCategoryTabLayout
        tabLayout.addOnTabSelectedListener(this)
        tabLayout.selectTab(tabLayout.getTabAt(tabPosition))

        binding.newsArticlesRv.adapter = NewsArticlesAdapter(viewModel)
        //TODO:handle extra divider at the bottom of the list
        val dividerItemDecoration =
            DividerItemDecoration(binding.newsArticlesRv.context, R.drawable.news_divider)
        binding.newsArticlesRv.addItemDecoration(dividerItemDecoration)
    }

    private fun initObservers() {

        viewModel.newsArticlesFlow.onEach {
            when (it) {
                is Resource.Error -> showErrorSnackbar()
                is Resource.Loading -> hideSnackbar()
                is Resource.Success -> hideSnackbar()
            }
        }.observeInLifecycle(viewLifecycleOwner)

        viewModel.events.onEach {
            when (it) {
                is HomeViewModel.Event.NavigateToDetails -> navigateToDetails(it.articleKey)
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun showErrorSnackbar() {
        view?.let {
            statusSnackbar =
                Snackbar.make(it, R.string.snackbar_news_fetch_Error_message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(it.context, R.color.snackbar_error))
            statusSnackbar.show()
        }
    }

    private fun hideSnackbar() {
        when {
            ::statusSnackbar.isInitialized -> statusSnackbar.dismiss()
            else -> return
        }
    }

    private fun navigateToDetails(articleKey: String) {
        if (findNavController().currentDestination?.id != R.id.homeFragment) return
        findNavController().navigate(HomeFragmentDirections.navigateToDetails(articleKey))
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            tabPosition = tab.position
            viewModel.onCategorySelected(tab.text.toString())
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?)  = Unit

    override fun onTabReselected(tab: TabLayout.Tab?) = Unit
}