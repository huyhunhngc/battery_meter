package io.github.ifa.glancewidget.di


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.reflect.KClass

@Qualifier
annotation class RepositoryQualifier

class RepositoryProvider @Inject constructor(
    @RepositoryQualifier val repositories: Map<Class<out Any>, @JvmSuppressWildcards Any>,
) {
    private val repositoriesMap = repositories
        .map { (k, v) ->
            k.kotlin to v as Any
        }.toMap()

    @Composable
    public fun Provide(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalRepositories provides repositoriesMap,
        ) {
            content()
        }
    }
}

@Suppress("CompositionLocalAllowlist")
val LocalRepositories = compositionLocalOf<Map<KClass<*>, Any>> {
    error("No LocalRepository provided")
}
