package com.example.newsapp.data.api

import com.example.newsapp.data.model.ArticleResponse
import com.example.newsapp.data.model.FilterParameters
import com.example.newsapp.data.model.SortByEnum
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import io.ktor.client.*
import io.ktor.client.request.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NewsApiImpl @Inject constructor(private val client: HttpClient) : INewsApi{

    override suspend fun searchNews(
        pageNumber: Int?,
        pageSize: Int?,
        filterParameters: FilterParameters
    ): ArticleResponse {
        return client.get("/top-headlines") {
            pageNumber?.let {
                parameter("page", it)
            }
            pageSize?.let {
                parameter("pageSize", it)
            }
            filterParameters.query?.let {
                parameter("q", it)
            }
            parameter("category", filterParameters.category.value)
            parameter("country", "us")
        }
    }
}