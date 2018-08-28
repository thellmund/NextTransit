package com.hellmund.transport.data.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface DestinationsDao {

    @Query("SELECT * FROM destination ORDER BY position")
    fun getAll(): LiveData<List<Destination>>

    @Query("SELECT COUNT(*) FROM destination")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg destination: Destination)

    @Update
    fun update(vararg destination: Destination)

    @Delete
    fun delete(destination: Destination)

}