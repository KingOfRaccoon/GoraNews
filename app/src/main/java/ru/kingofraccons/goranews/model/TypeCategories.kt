package ru.kingofraccons.goranews.model

// enum categories articles in news api
enum class TypeCategories(val category: Category) {
    Business(Category("business")),
    Entertainment(Category("entertainment")),
    General(Category("general")),
    Health(Category("health")),
    Science(Category("science")),
    Sports(Category("sports")),
    Technology(Category("technology"))
}