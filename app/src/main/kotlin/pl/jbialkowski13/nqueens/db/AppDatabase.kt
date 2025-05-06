package pl.jbialkowski13.nqueens.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        Score::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(
    value = [
        InstantConverter::class,
        DurationConverter::class
    ]
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
}

@Module
@InstallIn(SingletonComponent::class)
internal class AppDatabaseModule {

    @Provides
    @Singleton
    fun provide(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "nqueens-db")
            .openHelperFactory(FrameworkSQLiteOpenHelperFactory())
            .fallbackToDestructiveMigration(true)
            .build()
    }
}
