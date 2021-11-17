package com.example.newsapp.data.api

import com.example.newsapp.data.model.ArticleResponse
import com.example.newsapp.data.model.FilterParameters
import com.example.newsapp.data.model.SortByEnum
import io.ktor.client.features.observer.*
import io.ktor.http.cio.*

interface INewsApi {

    suspend fun searchNews(
        pageNumber: Int? = null,
        pageSize: Int? = null,
        filterParameters: FilterParameters = FilterParameters()
    ): ArticleResponse
}