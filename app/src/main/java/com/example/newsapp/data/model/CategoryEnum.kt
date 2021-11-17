package com.example.newsapp.data.model

enum class CategoryEnum(val value: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");
    companion object {
        fun getEnumByValue(value: String): CategoryEnum{
            return values().find {
                it.value == value
            } ?: GENERAL
        }
    }
}