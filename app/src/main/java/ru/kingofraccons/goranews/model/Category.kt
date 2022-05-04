package ru.kingofraccons.goranews.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// entity category article
@Entity
data class Category(@PrimaryKey var name: String = "")