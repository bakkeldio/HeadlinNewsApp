package com.example.newsapp.data.di

import com.example.newsapp.data.api.INewsApi
import com.example.newsapp.data.api.NewsApiImpl
import com.example.newsapp.data.repository.INewsRepository
import com.example.newsapp.data.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    abstract fun bindNewsApi(newsApiImpl: NewsApiImpl): INewsApi

    @Binds
    abstract fun bindNewsRepository(repositoryImpl: NewsRepositoryImpl): INewsRepository
}