package com.example.newsapp.data.repository

import com.example.newsapp.data.model.ArticleResponse
import com.example.newsapp.data.model.FilterParameters
import com.example.newsapp.data.model.Result

interface INewsRepository {

    suspend fun getNews(
        pageNumber: Int? = null,
        pageSize: Int? = null,
        filterParameters: FilterParameters
    ): Result<ArticleResponse>
}