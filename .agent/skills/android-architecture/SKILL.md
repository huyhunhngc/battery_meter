---
name: android-architecture
description: >
  A complete guide to building Android projects following Clean Architecture principles.
  Covers Hilt DI, Repository/UseCase patterns, type-safe Navigation, WorkManager,
  UI state management with StateFlow, CompositionLocals, and network setup with Ktorfit/Ktor.
---

# Android Clean Architecture Skill

This skill documents the exact architectural patterns used in this codebase. Follow these
patterns precisely when building a new project or migrating an existing one.

> **Rules (from GEMINI.md)**
> - Nearly-clean architecture principles
> - Hilt for dependency injection
> - Jetpack Compose for the View layer
> - No login required to use the app. Login is only required for sync / premium features.
> - **DO NOT ADD COMMENTS** in any code you write.

---

## 1. Project Structure

```
app/src/main/java/com/<org>/<app>/
├── Application.kt              ← @HiltAndroidApp entry point
├── MainActivity.kt             ← @AndroidEntryPoint, RepositoryProvider.Provide{}
├── MainViewModel.kt            ← App-level @HiltViewModel
├── ScannerApp.kt               ← Root composable (theme + AppNavHost)
│
├── background/                 ← WorkManager workers (@HiltWorker / @AssistedInject)
├── data/                       ← Repository implementations, API interfaces, DataStore
├── database/                   ← Room database, DAOs, entities
├── di/                         ← Hilt modules (common + feature-specific)
│   ├── common/                 ← App-wide modules (network, datastore, firebase, …)
│   └── <feature>/              ← Feature-scoped modules (e.g., DocumentRepositoryModule)
├── domain/                     ← Repository interfaces + UseCases
│   ├── repository/             ← Kotlin interfaces only
│   └── usecase/                ← UseCases + domain models under usecase/model/
├── features/                   ← Feature screens (composable routes + ViewModels)
├── glance/                     ← App-widget Glance composables
├── model/                      ← Shared data/request/response models
├── navigation/                 ← AppNavHost, NavigationMethod extensions, NavHostWithSlideEffect
├── service/                    ← NotificationHandler and similar services
├── ui/                         ← Design system (theme, components, CompositionLocals)
│   ├── component/              ← Reusable UI components
│   ├── localcomposition/       ← All CompositionLocal definitions
│   └── theme/                  ← Material3 theme, colors, typography
└── utils/                      ← Extension functions, helpers (including UiStateBuilder.kt)
```

---

## 2. Application Entry Point

```kotlin
@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appConfigManager: AppConfigManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        SomeWorker.enqueue(this)
        appConfigManager.fetchAndActivate()
    }
}
```

Key points:
- Annotate with `@HiltAndroidApp`.
- Implement `Configuration.Provider` and inject `HiltWorkerFactory` so WorkManager uses Hilt.
- Enqueue background workers and trigger remote-config fetches in `onCreate()`.

---

## 3. Dependency Injection (Hilt)

### 3.1 Hilt Components Used

| Component | When to use |
|---|---|
| `SingletonComponent` | App-scoped singletons (network, DB, repos) |
| `ActivityComponent` | Activity-scoped dependencies |
| `ViewModelComponent` | ViewModel-scoped dependencies (via `@HiltViewModel`) |
| `@EntryPoint` | Non-Hilt entry points (e.g., WorkManager workers) |

### 3.2 Module Convention

- **Common modules** go in `di/common/`: `NetworkModule`, `DataStoreModule`, `DatabaseModule`, `FirebaseModule`, `WorkerModule`.
- **Feature-specific modules** go in `di/<feature>/`, e.g., `di/document/DocumentRepositoryModule`.
- Prefer `@InstallIn(SingletonComponent::class)` for everything that is stateless or repository-level.

### 3.3 Qualifier Annotations

Define custom `@Qualifier` annotations alongside the module when multiple bindings of the same type exist:

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SummarizationKtorQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FileStorageKtorQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppSettingsDataStoreQualifier
```

Use `@Named("key")` only for simple string bindings (e.g., base URLs).

### 3.4 Network Module (Ktorfit + Ktor)

```kotlin
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Named("apiBaseUrl")
    @Provides
    fun provideBaseUrl(): String = BuildConfig.API_BASE_URL

    @Provides
    fun provideJson(): Json = defaultJson()

    @Provides
    fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        defaultKtorConfig(json)
    }

    @MyFeatureKtorQualifier
    @Provides
    fun provideMyFeatureKtorfit(
        json: Json,
        authDataStore: AuthDataStore,
        @Named("apiBaseUrl") apiBaseUrl: String,
    ): Ktorfit = Ktorfit.Builder().httpClient(
        HttpClient(OkHttp) {
            defaultKtorConfig(json)
            defaultRequest { contentType(ContentType.Application.Json) }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = authDataStore.getAccessToken(),
                            refreshToken = null,
                        )
                    }
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            expectSuccess = true
        }
    ).baseUrl(apiBaseUrl).build()
}
```

Put a `defaultKtorConfig(json: Json)` extension function in `di/common/DefaultKtorConfig.kt`
for shared Ktor client configuration (content negotiation, timeouts, etc.).

### 3.5 DataStore Module

```kotlin
@Qualifier annotation class AppSettingsDataStoreQualifier

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {

    @AppSettingsDataStoreQualifier
    @Provides
    @Singleton
    fun provideAppSettingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = createDataStore(
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        producePath = { context.cacheDir.resolve("app_settings.preferences_pb").path },
        context = context,
    )
}
```

### 3.6 Database Module

```kotlin
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()

    @Provides
    fun provideDocumentDao(db: AppDatabase): DocumentDao = db.documentDao()
}
```

### 3.7 Firebase Module

```kotlin
@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig
}
```

### 3.8 Repository Module with Multibinding (for CompositionLocal)

Use abstract module + `@Binds @IntoMap @ClassKey` to register every repository in a
`Map<Class<out Any>, Any>`. This map feeds `RepositoryProvider` which is used to expose
repositories via `CompositionLocal`.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DocumentRepositoryModule {

    @Binds
    @RepositoryQualifier
    @IntoMap
    @ClassKey(DocumentRepository::class)
    abstract fun bindDocumentRepository(repository: DocumentRepository): Any

    companion object {
        @Singleton
        @Provides
        fun provideDocumentRepository(
            @MyFeatureKtorQualifier ktorfit: Ktorfit,
            documentDao: DocumentDao,
            authDataStore: AuthDataStore,
        ): DocumentRepository = DefaultDocumentRepository(
            documentDao = documentDao,
            summarizeApi = ktorfit.createSummarizeApi(),
            authDataStore = authDataStore,
        )
    }
}
```

---

## 4. Domain Layer

### 4.1 Repository Interface

Located in `domain/repository/`. Contains only the contract — **no implementation**.

Each interface file also exports a `@Composable` accessor using `LocalRepositories`:

```kotlin
interface DocumentRepository {
    suspend fun addDocument(document: Document): Long
    fun getAllDocuments(): Flow<List<Document>>
    suspend fun deleteDocument(documentId: Long)
    fun getAllDocumentsPagingFlow(userId: String?): Flow<PagingData<DomainDocument>>
}

@Composable
fun localDocumentRepository(): DocumentRepository {
    return LocalRepositories.current[DocumentRepository::class] as DocumentRepository
}
```

The `@Composable` accessor is what the UI layer uses to obtain the repository without
needing Hilt injection in the composable (see §7 for full explanation).

### 4.2 UseCase

Located in `domain/usecase/`. Injected with `@Inject constructor`. No annotations other
than `@Inject`. Coordinates multiple repositories.

```kotlin
class PersistDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val fileStorageRepository: FileStorageRepository,
    private val historyActivityRepository: HistoryActivityRepository,
    @ApplicationContext private val context: Context,
    private val authDataStore: AuthDataStore,
) {
    suspend fun saveDocumentPdf(title: String, pdfFileUri: Uri): Long {
        val document = Document(
            title = title.ifBlank { "Untitled" },
            fileUri = pdfFileUri.toString(),
        )
        return documentRepository.addDocument(document)
    }
}
```

Rules:
- Only UseCases contain business logic that crosses domain boundaries.
- Repositories are injected into UseCases, never directly into ViewModels (prefer UseCase).
- ViewModels may inject repositories directly only for simple read-only flows.

### 4.3 Domain Models

Located in `domain/usecase/model/`. These are lightweight projections of DB entities:

```kotlin
data class DomainDocument(
    val id: Long,
    val title: String,
    val documentType: DocumentType,
)
```

Map from DB entity using an extension function in `utils/`:

```kotlin
fun Document.toDomain() = DomainDocument(id = id, title = title, documentType = documentType)
```

---

## 5. Data Layer

### 5.1 Repository Implementation

Located in `data/<feature>/Default<Name>Repository.kt`. Plain class — no Hilt annotation.
Instantiation happens in the DI module companion object.

```kotlin
class DefaultDocumentRepository(
    private val documentDao: DocumentDao,
    private val summarizeApi: SummarizeApi,
    private val authDataStore: AuthDataStore,
    private val context: Context,
    private val httpClient: HttpClient,
) : DocumentRepository {

    override fun getAllDocuments(): Flow<List<Document>> = documentDao.getAllDocuments()

    override fun getAllDocumentsPagingFlow(userId: String?): Flow<PagingData<DomainDocument>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = true),
            pagingSourceFactory = { documentDao.getAllDocumentsPagingSource(userId) }
        ).flow.map { it.map { doc -> doc.toDomain() } }

    override suspend fun addDocument(document: Document): Long = documentDao.insert(document)
}
```

### 5.2 Ktorfit API Interface

```kotlin
interface SummarizeApi {
    @POST("summarize")
    suspend fun summarizeText(@Body request: SummarizeRequest): SummarizeResponse

    @POST("translate")
    suspend fun translate(@Body request: TranslateRequest): TranslateResponse
}

fun Ktorfit.createSummarizeApi(): SummarizeApi = create()
```

### 5.3 DataStore Wrapper

Wrap DataStore behind a plain class for each concern:

```kotlin
class AppSettingDataStore @Inject constructor(
    @AppSettingsDataStoreQualifier private val dataStore: DataStore<Preferences>,
) {
    suspend fun getSummarizeSettings(): SummarizeSettings {
        val prefs = dataStore.data.first()
        return SummarizeSettings(maxSentences = prefs[MAX_SENTENCES_KEY] ?: 5)
    }

    suspend fun setSummarizeSettings(settings: SummarizeSettings) {
        dataStore.edit { it[MAX_SENTENCES_KEY] = settings.maxSentences }
    }

    companion object {
        private val MAX_SENTENCES_KEY = intPreferencesKey("max_sentences")
    }
}
```

---

## 6. Navigation

### 6.1 Type-Safe Routes

Use `@Serializable` data objects/classes as route descriptors (Navigation 2.8+):

```kotlin
@Serializable
object HomeScreen

@Serializable
data class DocumentDetailParams(val documentId: Long)

@Serializable
data class CameraCaptureParams(
    val target: Target = Target.TextScan,
) {
    enum class Target { TextScan, QRScan }
}
```

### 6.2 Feature Screen Registration

Each feature declares its own `NavGraphBuilder` extension:

```kotlin
fun NavGraphBuilder.documentDetailScreen(
    onBackClick: () -> Unit,
    onNavigateToPremium: () -> Unit,
) {
    composable<DocumentDetailParams> { backStackEntry ->
        val params = backStackEntry.toRoute<DocumentDetailParams>()
        DocumentDetailScreen(
            documentId = params.documentId,
            onBackClick = onBackClick,
            onNavigateToPremium = onNavigateToPremium,
        )
    }
}
```

### 6.3 AppNavHost

A single `AppNavHost` composable wires all routes. It wraps `NavHostWithSlideEffect`
(horizontal slide animation) inside a `SharedTransitionLayout` with a
`CompositionLocalProvider(LocalSharedTransitionScope provides this)`:

```kotlin
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHostWithSlideEffect(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier,
            ) {
                homeScreen(...)
                documentDetailScreen(
                    onBackClick = navController::safePopBackStack,
                    onNavigateToPremium = navController::navigateToSubscriptionScreen,
                )
            }
        }
    }
}
```

### 6.4 NavHostWithSlideEffect

A thin wrapper around `NavHost` that applies a horizontal slide with an ease-out cubic
bezier:

```kotlin
@Composable
fun NavHostWithSlideEffect(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    val easing = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350, easing = easing))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(350, easing = easing))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350, easing = easing))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(350, easing = easing))
        },
        builder = builder,
    )
}
```

### 6.5 NavigationMethod Extensions

All navigation actions are extension functions on `NavController` in `navigation/NavigationMethod.kt`:

```kotlin
fun NavController.safePopBackStack() {
    if (currentBackStackEntry?.destination?.route != homeScreenRoute) {
        popBackStack()
    }
}

fun NavController.navigateToSubscriptionScreen() = navigate(SubscriptionScreen)

fun NavController.navigateToDocumentDetail(params: DocumentDetailParams) {
    navigate(params) {
        launchSingleTop = true
    }
}
```

---

## 7. CompositionLocals and RepositoryProvider

### 7.1 RepositoryQualifier + RepositoryProvider

A `@RepositoryQualifier`-annotated Hilt multibinding collects all repositories into a map.
A `RepositoryProvider` class wraps this map and exposes it via a `CompositionLocal`:

```kotlin
@Qualifier
annotation class RepositoryQualifier

class RepositoryProvider @Inject constructor(
    @RepositoryQualifier val repositories: Map<Class<out Any>, @JvmSuppressWildcards Any>,
) {
    private val repositoriesMap = repositories.map { (k, v) -> k.kotlin to v }.toMap()

    @Composable
    fun Provide(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalRepositories provides repositoriesMap) {
            content()
        }
    }
}

val LocalRepositories = compositionLocalOf<Map<KClass<*>, Any>> {
    error("No LocalRepository provided")
}
```

In `MainActivity`, inject `RepositoryProvider` and call `repositoryProvider.Provide { … }` to
wrap the entire Compose tree:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var repositoryProvider: RepositoryProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            repositoryProvider.Provide {
                val isPremium by viewModel.isUserPremium.collectAsStateWithLifecycle()
                CompositionLocalProvider(LocalIsPremium provides isPremium) {
                    MyApp(startDestination = homeScreenRoute)
                }
            }
        }
    }
}
```

### 7.2 Repository Accessor in Domain Interface

Each domain repository interface declares its own `@Composable` accessor:

```kotlin
@Composable
fun localDocumentRepository(): DocumentRepository {
    return LocalRepositories.current[DocumentRepository::class] as DocumentRepository
}
```

Composables use this accessor to obtain the repository without Hilt, keeping UI components
loosely coupled:

```kotlin
@Composable
fun DocumentListScreen() {
    val repo = localDocumentRepository()
    val docs by repo.getAllDocuments().collectAsStateWithLifecycle(emptyList())
    LazyColumn { items(docs) { DocumentItem(it) } }
}
```

### 7.3 Other CompositionLocals

Create a `CompositionLocal` for each cross-cutting UI concern. Put them in `ui/localcomposition/`:

| File | Local | Purpose |
|---|---|---|
| `LocalIsPremium.kt` | `LocalIsPremium` | Whether the user has an active subscription |
| `LocalAuthUser.kt` | `LocalAuthUser` | Current authenticated user (nullable) |
| `LocalAppUsage.kt` | `LocalAppUsage` | Usage quota state |
| `LocalLoading.kt` | `LocalLoading` | Global loading state |
| `LocalChromeState.kt` | `LocalChromeState` | FAB visibility + scroll behavior |
| `LocalSharedTransitionScope.kt` | `LocalSharedTransitionScope` | Shared element transition scope |
| `LocalAnimatedVisibilityScope.kt` | `LocalAnimatedVisibilityScope` | Nested animated visibility scope |

**Pattern for a ChromeState example:**

```kotlin
data class ChromeState(
    val appBarScrollBehavior: TopAppBarScrollBehavior,
    val fabVisible: MutableState<Boolean>,
    val isInSelectedMode: MutableState<Boolean>,
)

val LocalChromeState = staticCompositionLocalOf<ChromeState> {
    error("No ChromeState provided")
}

@Composable
fun ProvideChromeState(content: @Composable () -> Unit) {
    val fabVisible = remember { mutableStateOf(true) }
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val chromeState = remember { ChromeState(appBarScrollBehavior, fabVisible, mutableStateOf(false)) }
    CompositionLocalProvider(LocalChromeState provides chromeState) {
        content()
    }
}
```

Use `staticCompositionLocalOf` for values that don't change frequently (equality check skips
recomposition). Use `compositionLocalOf` for values that change at runtime.

---

## 8. UI State Management

### 8.1 ViewModel Pattern

```kotlin
@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val myRepository: MyRepository,
    private val myUseCase: MyUseCase,
) : ViewModel() {

    data class UiState(
        val items: List<DomainItem> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )

    private val _items = myRepository.getItems().stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = buildUiState(_items, _isLoading) { items, isLoading ->
        UiState(items = items, isLoading = isLoading)
    }

    fun onDeleteItem(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            myUseCase.deleteItem(id)
            _isLoading.value = false
        }
    }
}
```

### 8.2 buildUiState Helper

Located in `utils/UiStateBuilder.kt`. Provides overloads for combining 1–5 `StateFlow`s
into a single UI state `StateFlow`:

```kotlin
fun <T1, R> ViewModel.buildUiState(
    flow: StateFlow<T1>,
    transform: (T1) -> R,
): StateFlow<R> = flow.map(transform)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = transform(flow.value),
    )

fun <T1, T2, R> ViewModel.buildUiState(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    transform: (T1, T2) -> R,
): StateFlow<R> = combine(flow, flow2, transform)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = transform(flow.value, flow2.value),
    )
```

Always use `WhileSubscribed(5_000)` to keep the flow alive 5 s after the last collector
drops (handles configuration changes).

### 8.3 Collecting State in Composables

```kotlin
@Composable
fun MyFeatureScreen(viewModel: MyFeatureViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyFeatureContent(
        items = uiState.items,
        isLoading = uiState.isLoading,
        onDeleteItem = viewModel::onDeleteItem,
    )
}
```

Pass lambdas and plain data down — never the ViewModel.

---

## 9. WorkManager

### 9.1 HiltWorker Pattern

Workers cannot receive Hilt injections via regular `@Inject constructor`. Use
`@HiltWorker` + `@AssistedInject`:

```kotlin
@HiltWorker
class MyWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val deps = EntryPoints.get(applicationContext, MyWorkerDependencies::class.java)
        val myRepo = deps.myRepository()

        try {
            myRepo.doSomething()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
```

### 9.2 EntryPoint for Workers

Declared in `di/common/WorkerModule.kt`:

```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface MyWorkerDependencies {
    fun myRepository(): MyRepository
    fun notificationHandler(): NotificationHandler
}
```

### 9.3 Work Request Factory Function

Declare a top-level function near the worker class:

```kotlin
fun createMyWorkRequest(inputParam: String): OneTimeWorkRequest {
    val data = workDataOf("param_key" to inputParam)
    return OneTimeWorkRequestBuilder<MyWorker>()
        .setInputData(data)
        .setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        )
        .addTag("my_work_tag")
        .build()
}
```

### 9.4 Worker Registration in Application

Provide `HiltWorkerFactory` via `Configuration.Provider` in `Application.kt` (see §2).

Enqueue work from the Application or ViewModel:

```kotlin
WorkManager.getInstance(context).enqueueUniqueWork(
    "unique_work_name",
    ExistingWorkPolicy.KEEP,
    createMyWorkRequest("value"),
)
```

For periodic work, add a companion `enqueue()` static method on the worker:

```kotlin
companion object {
    fun enqueue(context: Context) {
        val request = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "my_periodic_work",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
```

---

## 10. Hilt ViewModel and Feature Screen Setup

### 10.1 ViewModel

```kotlin
@HiltViewModel
class DocumentListViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
) : ViewModel() {

    data class UiState(val documents: PagingData<DomainDocument> = PagingData.empty())

    val pagingFlow: Flow<PagingData<DomainDocument>> =
        documentRepository.getAllDocumentsPagingFlow(userId = null).cachedIn(viewModelScope)
}
```

### 10.2 Route Descriptor

```kotlin
@Serializable
object DocumentListScreen
```

### 10.3 NavGraphBuilder Extension

```kotlin
fun NavGraphBuilder.documentListScreen(
    onBackClick: () -> Unit,
    onOpenDetail: (DocumentDetailParams) -> Unit,
) {
    composable<DocumentListScreen> {
        DocumentListScreen(onBackClick = onBackClick, onOpenDetail = onOpenDetail)
    }
}
```

### 10.4 Screen Composable

```kotlin
@Composable
fun DocumentListScreen(
    viewModel: DocumentListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenDetail: (DocumentDetailParams) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Documents") }, navigationIcon = { BackButton(onBackClick) }) }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(lazyItems.itemCount) { index ->
                lazyItems[index]?.let { doc ->
                    DocumentCard(doc, onClick = { onOpenDetail(DocumentDetailParams(doc.id)) })
                }
            }
        }
    }
}
```

---

## 11. Room Database

### 11.1 Entity

```kotlin
@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val documentType: DocumentType,
    val createdAt: Date,
    val fileUri: String,
    val isLocked: Boolean = false,
)
```

### 11.2 DAO

```kotlin
@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document): Long

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<Document>>

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocumentsPagingSource(userId: String?): PagingSource<Int, Document>

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)
}
```

### 11.3 Database

```kotlin
@Database(entities = [Document::class], version = 1, exportSchema = false)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
```

---

## 12. Summary Checklist for New Project

- [ ] Add `@HiltAndroidApp` to `Application`, implement `Configuration.Provider`
- [ ] Add `@AndroidEntryPoint` to `MainActivity`
- [ ] Create `di/common/NetworkModule` with Ktor/Ktorfit and qualifier annotations
- [ ] Create `di/common/DataStoreModule` with qualifier-per-store annotations
- [ ] Create `di/common/DatabaseModule` for Room
- [ ] Create `di/common/WorkerModule` with `@EntryPoint` interfaces per worker
- [ ] Create `di/common/RepositoryQualifier.kt` with `RepositoryProvider` and `LocalRepositories`
- [ ] For each feature: create domain repository interface in `domain/repository/` + `@Composable` accessor
- [ ] For each feature: create `DefaultXxxRepository` in `data/<feature>/` (plain class)
- [ ] For each feature: create `di/<feature>/XxxRepositoryModule` with `@Binds @IntoMap @ClassKey`
- [ ] Create UseCases in `domain/usecase/` with `@Inject constructor`
- [ ] Create `utils/UiStateBuilder.kt` with `buildUiState` overloads
- [ ] Create `navigation/NavHostWithSlideEffect.kt`
- [ ] Create `navigation/NavigationMethod.kt` for typed extension functions
- [ ] Create `navigation/AppNavHost.kt` with all routes
- [ ] Set up `CompositionLocal` files in `ui/localcomposition/` for cross-cutting state
- [ ] Wrap entire Compose tree in `repositoryProvider.Provide {}` inside `MainActivity`
- [ ] Use `@HiltWorker` + `@AssistedInject` for all WorkManager workers
