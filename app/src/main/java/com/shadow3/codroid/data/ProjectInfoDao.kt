package com.shadow3.codroid.data

import androidx.navigation.Navigator
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectInfoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: ProjectInfo)

    @Update
    suspend fun update(item: ProjectInfo)

    @Delete
    suspend fun delete(item: ProjectInfo)

    @Query("SELECT * from projects WHERE name = :name")
    fun getProject(name: String): Flow<ProjectInfo>

    @Query("SELECT * from projects")
    fun getAllProjects(): Flow<List<ProjectInfo>>

    @Query("SELECT EXISTS(SELECT 1 FROM projects WHERE name = :projectName)")
    suspend fun exists(projectName: String): Boolean
}
