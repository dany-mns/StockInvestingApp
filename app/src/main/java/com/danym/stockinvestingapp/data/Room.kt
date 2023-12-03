package com.danym.stockinvestingapp.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import java.time.LocalDate


@Entity(tableName = "stocks", primaryKeys = ["stockTicker", "date"])
data class StockEntity(
    val stockTicker: String, val price: Double, val date: LocalDate
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}

@Database(entities = [StockEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, AppDatabase::class.java, "app_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return INSTANCE!!
            }
        }
    }
}

@Dao
interface StockDao {
    @Upsert
    suspend fun insertStock(stock: StockEntity)

    @Query("SELECT * FROM stocks WHERE stockTicker = :ticker ORDER BY date DESC LIMIT :limitN")
    fun getLastNStockData(ticker: String, limitN: Int): List<StockEntity>?
}