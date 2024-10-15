package com.shadow3.codroid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProjectInfo::class], version = 1, exportSchema = false)
abstract class ProjectInfoRoomDataBase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: ProjectInfoRoomDataBase? = null

        fun getDatabase(context: Context): ProjectInfoRoomDataBase {
            return (INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectInfoRoomDataBase::class.java,
                    "item_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance

                return instance
            })
        }
    }

    abstract fun projectInfoDao(): ProjectInfoDao
}