package com.danym.stockinvestingapp.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import java.time.LocalDate
import java.util.Date


@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val stockTicker: String,
    val prices: Double,
//    val date: Date
)

@Database(entities = [StockEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                }
                return INSTANCE!!
            }
        }
    }
}

// Define your DAO (Data Access Object)
@Dao
interface StockDao {
    @Insert
    suspend fun insertStock(stock: StockEntity)

}