package com.example.newsapp.data.model

import kotlinx.serialization.Serializable

@Serializable
class ArticleResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)