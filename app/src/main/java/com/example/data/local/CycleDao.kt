package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Query("SELECT * FROM wash_cycles ORDER BY timestamp DESC")
    fun getAllCycles(): Flow<List<CycleEntity>>

    @Query("SELECT COUNT(*) FROM wash_cycles")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cycles: List<CycleEntity>)

    @Query("DELETE FROM wash_cycles")
    suspend fun deleteAll()
}
