package com.afterapps.fortnightly.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.afterapps.fortnightly.R
import com.afterapps.fortnightly.databinding.FragmentDetailsBinding
import com.afterapps.fortnightly.util.observeInLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val viewModel: DetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)

        initViews(binding)

        initObservers(binding)

    }

    private fun initViews(binding: FragmentDetailsBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun initObservers(binding: FragmentDetailsBinding) {
        viewModel.events.onEach {
            when (it) {
                is DetailsViewModel.Event.OpenArticleUrl -> openArticleUrl(it.articleUrl)
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun openArticleUrl(articleUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
        startActivity(intent)
    }

}