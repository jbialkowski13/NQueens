package pl.jbialkowski13.nqueens.score

internal sealed interface SaveScoreResult {
    data object Success : SaveScoreResult
    data object Failure : SaveScoreResult
}
