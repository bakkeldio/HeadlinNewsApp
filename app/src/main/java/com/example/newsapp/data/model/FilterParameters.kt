package com.example.newsapp.data.model

data class FilterParameters(
    val query: String? = null,
    val category: CategoryEnum = CategoryEnum.GENERAL
)