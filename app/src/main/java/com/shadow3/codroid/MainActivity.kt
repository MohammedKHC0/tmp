@file:Suppress("DEPRECATION")

package com.shadow3.codroid

import android.app.LocalActivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.dependency
import com.shadow3.codroid.composable.SideBar
import com.shadow3.codroid.data.ProjectInfoRoomDataBase
import com.shadow3.codroid.ui.theme.CodroidTheme
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        const val TERMINAL_CHANNEL = "com.shadow3.codroid/terminal"

        init {
            System.loadLibrary("rust")
            System.loadLibrary("mterm")
        }
    }

    private lateinit var flutterEngine: FlutterEngine

    private val mLocalActivityManager = LocalActivityManager(this, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache
            .getInstance()
            .put("main_activity", flutterEngine)
        mLocalActivityManager.dispatchCreate(savedInstanceState)

        val terminalMethodChannel =
            MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                TERMINAL_CHANNEL
            )

        setContent {
            CodroidTheme {
                App(
                    localActivityManager = mLocalActivityManager,
                    terminalMethodChannel = terminalMethodChannel
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mLocalActivityManager.dispatchPause(isFinishing)
    }

    override fun onResume() {
        super.onResume()
        mLocalActivityManager.dispatchResume()
    }
}

@Composable
fun App(
    localActivityManager: LocalActivityManager,
    terminalMethodChannel: MethodChannel
) {
    AppContent(
        appViewModel = viewModel(),
        localActivityManager = localActivityManager,
        terminalMethodChannel = terminalMethodChannel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    appViewModel: AppViewModel,
    localActivityManager: LocalActivityManager,
    terminalMethodChannel: MethodChannel,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val projectInfoDatabase = remember { ProjectInfoRoomDataBase.getDatabase(context = context) }
    appViewModel.setup(context)

    ModalNavigationDrawer(drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                SideBar()
            }
        }) {
        Scaffold(
            topBar = {
                val appTitle by appViewModel.appTitle.collectAsState()
                TopAppBar(
                    title = { Text(text = appTitle) },
                    colors = TopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                )
            },
        ) { innerPadding ->
            DestinationsNavHost(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
                navGraph = NavGraphs.root,
                dependenciesContainerBuilder = {
                    dependency(projectInfoDatabase)
                    dependency(appViewModel)
                    dependency(localActivityManager)
                    dependency(terminalMethodChannel)
                })
        }
    }
}
