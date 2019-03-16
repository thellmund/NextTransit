package com.hellmund.transport.data.persistence

import androidx.room.*
import io.reactivex.Observable

@Dao
interface DestinationsDao {

    @Query("SELECT * FROM destination ORDER BY position")
    fun getAll(): Observable<List<Destination>>

    @Query("SELECT COUNT(*) FROM destination")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg destination: Destination)

    @Update
    fun update(vararg destination: Destination)

    @Delete
    fun delete(destination: Destination)

}