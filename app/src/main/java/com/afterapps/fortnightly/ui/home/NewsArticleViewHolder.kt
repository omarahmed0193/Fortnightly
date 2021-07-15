package com.afterapps.fortnightly.ui.home

import coil.load
import com.afterapps.fortnightly.databinding.ListItemNewsArticleBinding
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.util.BaseViewHolder

class NewsArticleViewHolder(
    private val binding: ListItemNewsArticleBinding,
    private val newsArticleReactor: NewsArticleReactor
) :
    BaseViewHolder<NewsArticle>(binding.root) {
    override fun bind(item: NewsArticle) {
        binding.article = item
        binding.newsArticleReactor = newsArticleReactor
        binding.executePendingBindings()
    }
}