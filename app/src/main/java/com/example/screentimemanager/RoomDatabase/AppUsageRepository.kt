package com.example.screentimemanager.RoomDatabase
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//Implemented ExerciseRepository by Referring to the Professor's code
class AppUsageRepository(private val exerciseDatabaseDao: AppUsageDatabaseDao) {

    val allExercise: Flow<List<AppUsageSchema>> = exerciseDatabaseDao.getAllAppUsage()

    fun insert(exercise: AppUsageSchema){
        CoroutineScope(IO).launch{
            exerciseDatabaseDao.insertUsage(exercise)
        }
    }
    fun delete(id: Long){
        CoroutineScope(IO).launch {
            //exerciseDatabaseDao.deleteUsage(id)
        }
    }
    fun deleteAll(){
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.deleteAllUsage()
        }
    }
}