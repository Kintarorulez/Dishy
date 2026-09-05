package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CycleEntity::class], version = 1, exportSchema = false)
abstract class DishyDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao

    companion object {
        @Volatile
        private var INSTANCE: DishyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DishyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DishyDatabase::class.java,
                    "dishy_database"
                )
                .addCallback(DishyDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance

                scope.launch(Dispatchers.IO) {
                    if (instance.cycleDao().getCount() < 100) {
                        populateInitialCycles(instance.cycleDao(), forceRefresh = false)
                    }
                }

                instance
            }
        }

        suspend fun populateInitialCycles(dao: CycleDao, forceRefresh: Boolean = false) {
            if (forceRefresh) {
                dao.deleteAll()
            } else if (dao.getCount() >= 100) {
                return
            }

            val list = mutableListOf<CycleEntity>()

            val programs = listOf(
                Triple("Eco50", "Eco 50° Cycle", 75 to (9.4 to 0.82)),
                Triple("Auto2", "Auto 45-65°", 60 to (11.2 to 1.05)),
                Triple("Quick45", "Quick 45°", 25 to (10.0 to 0.80)),
                Triple("Silence50", "Silence 50°", 95 to (9.8 to 0.78)),
                Triple("Intensiv70", "Intensive 70°", 75 to (14.2 to 1.35)),
                Triple("Glass40", "Glass 40°", 45 to (10.8 to 0.75))
            )

            // Populate overall cycle history for the last year (12 rolling months)
            for (offset in 11 downTo 0) {
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.MONTH, -offset)
                val targetYear = cal.get(java.util.Calendar.YEAR)
                val targetMonth = cal.get(java.util.Calendar.MONTH)
                val maxDay = if (offset == 0) {
                    cal.get(java.util.Calendar.DAY_OF_MONTH).coerceAtLeast(1)
                } else {
                    cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                }

                // Between 20 and 28 cycles per month across the past year
                val monthlyCyclesCount = if (offset == 0) {
                    ((maxDay * 0.7).toInt()).coerceIn(12, 26)
                } else {
                    20 + ((targetMonth * 7) % 9)
                }

                for (i in 1..monthlyCyclesCount) {
                    val day = ((i * 29) % maxDay) + 1
                    val cycleCal = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, targetYear)
                        set(java.util.Calendar.MONTH, targetMonth)
                        set(java.util.Calendar.DAY_OF_MONTH, day)
                        set(java.util.Calendar.HOUR_OF_DAY, 8 + (i % 14))
                        set(java.util.Calendar.MINUTE, (i * 17) % 60)
                        set(java.util.Calendar.SECOND, (i * 31) % 60)
                    }

                    val prog = programs[i % programs.size]
                    list.add(
                        CycleEntity(
                            programKey = prog.first,
                            programName = prog.second,
                            timestamp = cycleCal.timeInMillis,
                            durationMinutes = prog.third.first,
                            waterLiters = prog.third.second.first,
                            energyKwh = prog.third.second.second,
                            efficiencyRating = if (prog.first == "Silence50") "A+++" else if (prog.first == "Intensiv70") "A" else "A++",
                            status = "Completed",
                            isScheduled = (i % 4 == 0)
                        )
                    )
                }
            }
            dao.insertAll(list)
        }
    }

    private class DishyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialCycles(database.cycleDao())
                }
            }
        }
    }
}
