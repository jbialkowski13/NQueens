package pl.jbialkowski13.nqueens.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import pl.jbialkowski13.nqueens.coroutines.CoroutineDispatchers

internal class CoroutinesExtension(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : BeforeEachCallback, AfterEachCallback, ParameterResolver {

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }

    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean {
        return parameterContext.isCoroutineDispatchers()
    }

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any? {
        return if (parameterContext.isCoroutineDispatchers()) {
            TestCoroutineDispatchers(dispatcher)
        } else {
            error("Not supported type ${parameterContext.parameter.type}")
        }
    }

    private fun ParameterContext.isCoroutineDispatchers() =
        parameter.type == CoroutineDispatchers::class.java
}

private class TestCoroutineDispatchers(
    dispatcher: CoroutineDispatcher
) : CoroutineDispatchers {
    override val default: CoroutineDispatcher = dispatcher
}
