
package com.example.screentimemanager.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.screentimemanager.RoomDatabase.AppUsageSchema
import kotlinx.coroutines.flow.Flow
//Implemented ExerciseDatabaseDao by Referring to the Professor's code
@Dao
interface AppUsageDatabaseDao {

    @Insert
    suspend fun insertUsage(usage: AppUsageSchema)

    @Query("SELECT * FROM AppUsage")
    fun getAllAppUsage(): Flow<List<AppUsageSchema>>

    @Query("DELETE FROM AppUsage")
    suspend fun deleteAllUsage()

    @Query("DELETE FROM AppUsage WHERE applicationID = :key")
    suspend fun deleteUsage(key: Long)

}