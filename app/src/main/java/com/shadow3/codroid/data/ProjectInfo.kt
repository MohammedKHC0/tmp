package com.shadow3.codroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "projects")
data class ProjectInfo(
    @PrimaryKey
    val name: String,
    val description: String?,
    val type: ProjectType,
    val path: String,
)
