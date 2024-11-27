package pro.wolfnord.lab11

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.launch
import pro.wolfnord.lab11.ui.theme.Lab11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab11Theme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") { MyScreenContent(navController) }
                    composable("search") { SearchScreen() }
                    composable("share") { ShareScreen() }
                }
            }
        }
    }
}

class SimpleWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // Здесь вы можете выполнять вашу фоновую задачу
        Log.d("SimpleWorker", "Work is being done")
        // Возвращаем результат
        return Result.success()
    }
}

@Composable
fun MyScreenContent(navController: NavController) {
    val textState = remember { mutableStateOf("Hello, World!") }
    val scaffoldState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Получаем контекст
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopApp(
                title = "Jetpack Compose",
                scaffoldState = scaffoldState
            )
            if (scaffoldState.isOpen) {
                ModalDrawerSheet {
                    Column(Modifier.padding(16.dp)) {
                        Text("Item 1")
                        Text("Item 2")
                        Text("Item 3")
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(textState.value)

                Button(
                    onClick = {
                        textState.value = "Button clicked"
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Click me")
                }

                Button(
                    onClick = {
                        val workRequest = OneTimeWorkRequestBuilder<SimpleWorker>().build()
                        WorkManager.getInstance(context).enqueue(workRequest)
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Start WorkManager Task")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { textState.value = "Fab clicked" },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
            }
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                Spacer(Modifier.weight(1f, true))
                IconButton(onClick = { /* Handle icon click */ }) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
                }
                IconButton(onClick = { navController.navigate("share") }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share")
                }
            }
        }
    )
}

@Composable
fun ShareScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Share functionality will be added here.")
    }
}

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Search functionality will be added here.")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopApp(title: String, scaffoldState: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                modifier = Modifier.clickable { /* Handle Home click */ }
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.clickable {
                    scope.launch {
                        if (scaffoldState.isOpen) {
                            scaffoldState.close()
                        } else {
                            scaffoldState.open()
                        }
                    }
                }
            )
        }
    )
}
