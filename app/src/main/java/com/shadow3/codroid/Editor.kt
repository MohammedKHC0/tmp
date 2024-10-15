@file:Suppress("DEPRECATION")

package com.shadow3.codroid

import android.app.LocalActivityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.NavHostParam
import com.shadow3.codroid.composable.CodeEditor
import com.shadow3.codroid.composable.DirectoryFilesList
import com.shadow3.codroid.composable.PathCard
import com.shadow3.codroid.data.ProjectInfo
import com.shadow3.codroid.data.ProjectType
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.launch

@Destination<RootGraph>
@Composable
fun Editor(
    projectInfo: ProjectInfo,
    @NavHostParam localActivityManager: LocalActivityManager,
    @NavHostParam terminalMethodChannel: MethodChannel,
) {
    EditorContent(
        projectInfo = projectInfo,
        viewModel = viewModel(factory = EditorViewModelFactory(projectInfo = projectInfo)),
        localActivityManager = localActivityManager,
        terminalMethodChannel = terminalMethodChannel
    )
}

@Composable
fun EditorContent(
    projectInfo: ProjectInfo = ProjectInfo(
        "Kotlin", "preview", ProjectType.Kotlin, "/sdcard"
    ),
    viewModel: EditorViewModel,
    localActivityManager: LocalActivityManager,
    terminalMethodChannel: MethodChannel
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (viewModel.editor == null) {
                    viewModel.initEditor(context = LocalContext.current)
                }

                CodeEditor(
                    modifier = Modifier
                        .fillMaxSize(),
                    editor = viewModel.editor!!,
                    state = viewModel.editorState,
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                EditorBottomTools(viewModel = viewModel)
            }

            val showBottomSheet by viewModel.showBottomSheet.collectAsState()

            if (showBottomSheet) {
                BottomSheet(
                    projectInfo = projectInfo,
                    viewModel = viewModel,
                    localActivityManager = localActivityManager,
                    terminalMethodChannel = terminalMethodChannel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    projectInfo: ProjectInfo,
    viewModel: EditorViewModel,
    localActivityManager: LocalActivityManager,
    terminalMethodChannel: MethodChannel,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val pagerState = rememberPagerState { 3 }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { viewModel.setShowBottomSheet(show = false) },
        sheetState = bottomSheetState
    ) {
        Column {
            TabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                Tab(text = {
                    Text(text = "terminal")
                }, selected = pagerState.currentPage == 0, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                })

                Tab(text = {
                    Text(text = "opened files")
                }, selected = pagerState.currentPage == 1, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })

                Tab(text = {
                    Text(text = "project")
                }, selected = pagerState.currentPage == 2, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                })
            }

            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        Column {
                            AndroidView(
                                factory = { context ->
                                    val view = localActivityManager.startActivity(
                                        "terminal",
                                        FlutterActivity.withCachedEngine("main_activity")
                                            .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                                            .build(context)
                                    ).decorView
                                    viewModel.prootLogin(methodChannel = terminalMethodChannel)
                                    view
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(5.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            )
                            TerminalBottomTools(
                                viewModel = viewModel, methodChannel = terminalMethodChannel
                            )
                        }
                    }

                    1 -> {
                        val currentOpenFile by viewModel.currentOpenedFile.collectAsState()
                        val editorStates by viewModel.editorStates.collectAsState()
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = editorStates.keys.toList()) { path ->
                                if (currentOpenFile == path) {
                                    PathCard(
                                        modifier = Modifier
                                            .height(60.dp)
                                            .fillMaxWidth(),
                                        path = path,
                                        contentColor = MaterialTheme.colorScheme.primary,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        onClick = {
                                            viewModel.openFile(context = context, path = path)
                                        })
                                } else {
                                    PathCard(modifier = Modifier
                                        .height(60.dp)
                                        .fillMaxWidth(), path = path, onClick = {
                                        viewModel.openFile(context = context, path = path)
                                    })
                                }
                            }
                        }
                    }

                    2 -> {
                        DirectoryFilesList(modifier = Modifier.fillMaxSize(),
                            path = projectInfo.path,
                            onClick = {
                                viewModel.openFile(context = context, path = it)
                            })
                    }

                    else -> {
                        BadGuys()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun BadGuys() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "THIS IS AN ILLEGAL PAGE", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(100.dp))
        Icon(
            modifier = Modifier.size(300.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_bad_guys),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "WHAT DID YOU DO?", style = MaterialTheme.typography.titleLarge)
    }
}


@Composable
fun EditorBottomTools(viewModel: EditorViewModel) {
    Surface(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val editor = viewModel.editor

            EditorBottomKey(
                onClick = { editor?.insertText("    ", 4) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "Tab",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("/", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "/",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("#", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "#",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("[]", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "[",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("]", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "]",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("()", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "(",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText(")", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = ")",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("{}", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "{",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { editor?.insertText("}", 1) },
                content = {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "}",
                        textAlign = TextAlign.Center
                    )
                }
            )

            EditorBottomKey(
                onClick = { viewModel.setShowBottomSheet(true) },
                content = {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = ImageVector.vectorResource(id = R.drawable.icon_windows_up),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun RowScope.EditorBottomKey(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(35.dp)
            .weight(1f),
        onClick = onClick,
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun TerminalBottomTools(viewModel: EditorViewModel? = null, methodChannel: MethodChannel? = null) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, key = InputKeys.Esc
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "ESC",
                        textAlign = TextAlign.Center
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, key = InputKeys.Home
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "HOME",
                        textAlign = TextAlign.Center
                    )
                }

                val altState = if (viewModel == null) {
                    false
                } else {
                    val inner by viewModel.altState.collectAsState()
                    inner
                }
                val altContentColor = if (altState) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                TerminalBottomKey(contentColor = altContentColor, onClick = {
                    viewModel?.shiftAltState(methodChannel = methodChannel!!)
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "ALT",
                        textAlign = TextAlign.Center
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.ArrowUp
                    )
                }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.End
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "END",
                        textAlign = TextAlign.Center
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.PageUp
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "PGUP",
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, key = InputKeys.Tab
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "TAB",
                        textAlign = TextAlign.Center
                    )
                }

                val controlState = if (viewModel == null) {
                    false
                } else {
                    val inner by viewModel.controlState.collectAsState()
                    inner
                }

                val controlContentColor = if (controlState) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                TerminalBottomKey(contentColor = controlContentColor, onClick = {
                    viewModel?.shiftControlState(methodChannel = methodChannel!!)
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "CTRL",
                        textAlign = TextAlign.Center
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.ArrowLeft
                    )
                }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.ArrowDown
                    )
                }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.ArrowRight
                    )
                }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }

                TerminalBottomKey(onClick = {
                    viewModel?.inputKey(
                        methodChannel = methodChannel!!, InputKeys.PageDown
                    )
                }) {
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentSize(align = Alignment.Center),
                        fontSize = 15.sp,
                        text = "PGDN",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.TerminalBottomKey(
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .height(35.dp)
            .weight(1f), onClick = onClick, colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
