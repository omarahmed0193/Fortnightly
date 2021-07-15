package com.afterapps.fortnightly.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.afterapps.fortnightly.R
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.model.domain.NewsArticlesDiffCallback
import com.afterapps.fortnightly.util.BaseViewHolder

class NewsArticlesAdapter(private val newsArticleReactor: NewsArticleReactor) :
    ListAdapter<NewsArticle, BaseViewHolder<NewsArticle>>(NewsArticlesDiffCallback) {

    companion object {
        private const val TYPE_HEADLINE = 0
        private const val TYPE_ARTICLE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<NewsArticle> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADLINE -> NewsArticleHeadlineViewHolder(
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.list_item_news_article_headline,
                    parent,
                    false
                ), newsArticleReactor
            )
            TYPE_ARTICLE -> NewsArticleViewHolder(
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.list_item_news_article,
                    parent,
                    false
                ), newsArticleReactor
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADLINE
            else -> TYPE_ARTICLE
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<NewsArticle>, position: Int) {
        val newsArticle = getItem(position)
        newsArticle?.let { holder.bind(newsArticle) }
    }
}

interface NewsArticleReactor {
    fun onNewsArticleClick(articleKey: String)
}