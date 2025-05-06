package pl.jbialkowski13.nqueens.utils

import androidx.room.InvalidationTracker
import pl.jbialkowski13.nqueens.db.AppDatabase
import pl.jbialkowski13.nqueens.db.ScoreDao

internal class FakeAppDatabase(
    private val fakeScoreDao: FakeScoreDao = FakeScoreDao()
) : AppDatabase() {

    override fun scoreDao(): ScoreDao {
        return fakeScoreDao
    }

    override fun clearAllTables() {
        // do nothing
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this)
    }
}
