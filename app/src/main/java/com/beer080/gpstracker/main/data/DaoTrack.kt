package com.beer080.gpstracker.main.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DaoTrack {
    @Insert
    suspend fun addTrack(trackItem: TrackItem)

    @Query("SELECT * FROM TRACKS")
    fun getAllTracks(): Flow<List<TrackItem>>


}