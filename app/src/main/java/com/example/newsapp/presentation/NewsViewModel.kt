package com.example.newsapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.ArticleResponse
import com.example.newsapp.data.model.CategoryEnum
import com.example.newsapp.data.model.FilterParameters
import com.example.newsapp.data.model.Result
import com.example.newsapp.data.repository.INewsRepository
import com.example.newsapp.presentation.model.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: INewsRepository) : ViewModel() {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 25
    }

    val stateFlow: MutableStateFlow<ResourceState<Pair<Boolean, ArticleResponse>>> =
        MutableStateFlow(ResourceState.Loading)

    private var paging: Paging = Paging()

    fun searchNews(
        query: String? = null,
        category: CategoryEnum = CategoryEnum.GENERAL,
        isUpdate: Boolean = false,
        isPaging: Boolean = false
    ) {
        if (isUpdate) {
            resetPaging()
        }
        if (!hasNextPage()) {
            return
        }
        val page = getNextPage() ?: return
        stateFlow.value = ResourceState.Loading
        viewModelScope.launch {

            when (val response = repository.getNews(
                page,
                DEFAULT_PAGE_SIZE,
                FilterParameters(query, category)
            )) {
                is Result.Success -> {
                    updatePagination(response.data.totalResults, page)
                    stateFlow.value =
                        if (response.data.articles.isEmpty()) ResourceState.Empty else ResourceState.Success(
                            Pair(isPaging, response.data)
                        )
                }
                is Result.Error -> {
                    stateFlow.value = ResourceState.Error(response.message)
                }
            }
        }
    }

    private fun updatePagination(totalResults: Int, pageNumber: Int) {
        paging.totalResults = totalResults
        paging.pagesCount = totalResults / DEFAULT_PAGE_SIZE
        paging.currentPage = pageNumber
        paging.hasNextPage = paging.pagesCount > paging.currentPage
    }

    private fun resetPaging() {
        paging = Paging()
    }


    private fun getNextPage(): Int? {
        return if (hasNextPage()) {
            paging.currentPage += 1
            paging.currentPage
        } else {
            null
        }
    }

    private fun hasNextPage(): Boolean {
        return paging.hasNextPage
    }

    inner class Paging(
        var currentPage: Int = 0,
        var hasNextPage: Boolean = true,
        var totalResults: Int = 0,
        var pagesCount: Int = 0
    )
}