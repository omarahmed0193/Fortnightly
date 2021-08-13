package com.afterapps.fortnightly.binding

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.afterapps.fortnightly.model.domain.NewsArticle
import com.afterapps.fortnightly.ui.home.NewsArticlesAdapter
import com.afterapps.fortnightly.util.Resource

@BindingAdapter("imgUrl")
fun bindImgUrlToImageView(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        imageView.load(it) {
            crossfade(true)
        }
    }
}

@BindingAdapter("newsData")
fun bindNewsDataToRecyclerViews(recyclerView: RecyclerView, newsArticles: List<NewsArticle>?) {
    newsArticles?.let {
        val adapter = recyclerView.adapter as NewsArticlesAdapter
        adapter.submitList(it)
    }
}

@BindingAdapter("resourceStatusProgress")
fun bindResourceStatusLoadingToView(view: View, status: Resource<*>?) {
    view.isVisible = status is Resource.Loading
}

@BindingAdapter("dataResourceStatus")
fun bindDataResourceStatusToView(recyclerView: RecyclerView, status: Resource<List<NewsArticle>>?) {
    status?.let {
        it.data?.let { resourceData ->
            recyclerView.isVisible = !resourceData.isNullOrEmpty() && status !is Resource.Loading<*>
        }
    }
}

