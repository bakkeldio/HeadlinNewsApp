package com.example.newsapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.ItemNewsBinding


class NewsAdapter(private val listener: Listener) : ListAdapter<Article, NewsAdapter.NewsViewHolder>(Callback) {


    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Article) {
            Glide.with(binding.root.context)
                .load(model.urlToImage)
                .error(R.drawable.news_default_image)
                .placeholder(R.drawable.news_default_image)
                .into(binding.newsLogo)

            binding.title.text = model.title
            binding.root.setOnClickListener {
                listener.onNewsClick(model)
            }
        }
    }


    object Callback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onNewsClick(item: Article)
    }


}