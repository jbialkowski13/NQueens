package pl.jbialkowski13.nqueens.game

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import kotlin.math.abs

internal interface ConflictsResolver {
    fun resolve(queens: Set<Position>, position: Position): Set<Position>
}

internal class ConflictsResolverImpl @Inject constructor() : ConflictsResolver {

    override fun resolve(queens: Set<Position>, position: Position): Set<Position> {
        return queens.filterTo(mutableSetOf()) { otherPos -> position.conflictsWith(otherPos) }
    }

    private fun Position.conflictsWith(other: Position): Boolean {
        return this.row == other.row ||
                this.column == other.column ||
                abs(this.row - other.row) == abs(this.column - other.column)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface ConflictsResolverModule {

    @Binds
    fun bind(impl: ConflictsResolverImpl): ConflictsResolver
}
