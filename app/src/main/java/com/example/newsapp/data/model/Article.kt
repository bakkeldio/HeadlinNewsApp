package com.example.newsapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val author: String? = null,
    val title: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    val content: String? = null
)