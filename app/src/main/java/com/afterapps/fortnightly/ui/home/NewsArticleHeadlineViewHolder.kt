package com.afterapps.fortnightly.ui.home

import com.afterapps.fortnightly.databinding.ListItemNewsArticleHeadlineBinding
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.util.BaseViewHolder

class NewsArticleHeadlineViewHolder(
    private val binding: ListItemNewsArticleHeadlineBinding,
    private val newsArticleReactor: NewsArticleReactor
) :
    BaseViewHolder<NewsArticle>(binding.root) {
    override fun bind(item: NewsArticle) {
        binding.article = item
        binding.newsArticleReactor = newsArticleReactor
        binding.executePendingBindings()
    }
}