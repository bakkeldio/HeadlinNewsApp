package com.example.newsapp.data.repository

import com.example.newsapp.data.api.INewsApi
import com.example.newsapp.data.model.ArticleResponse
import com.example.newsapp.data.model.FilterParameters
import com.example.newsapp.data.model.Result
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NewsRepositoryImpl @Inject constructor(private val newsApi: INewsApi) : INewsRepository {
    override suspend fun getNews(
        pageNumber: Int?,
        pageSize: Int?,
        filterParameters: FilterParameters
    ): Result<ArticleResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response =
                    newsApi.searchNews(pageNumber, pageSize, filterParameters)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "")
            }
        }
    }

}