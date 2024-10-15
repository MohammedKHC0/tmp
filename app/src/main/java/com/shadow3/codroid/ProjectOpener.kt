package com.shadow3.codroid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.NavHostParam
import com.ramcosta.composedestinations.generated.destinations.EditorDestination
import com.ramcosta.composedestinations.generated.destinations.ProjectCreatorDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.shadow3.codroid.composable.ProjectCard
import com.shadow3.codroid.composable.ProjectIcon
import com.shadow3.codroid.data.ProjectInfo
import com.shadow3.codroid.data.ProjectInfoRoomDataBase
import com.shadow3.codroid.data.ProjectType
import kotlinx.coroutines.launch

@Composable
@Destination<RootGraph>
fun ProjectOpener(
    navController: DestinationsNavigator,
    @NavHostParam
    appViewModel: AppViewModel,
    @NavHostParam
    projectInfoDatabase: ProjectInfoRoomDataBase
) {
    val viewModel: ProjectOpenerViewModel =
        viewModel(factory = ProjectOpenerViewModelFactory(projectInfoDatabase.projectInfoDao()))
    ProjectOpenerContent(
        navController = navController,
        appViewModel = appViewModel,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProjectOpenerContent(
    navController: DestinationsNavigator? = null,
    appViewModel: AppViewModel? = null,
    viewModel: ProjectOpenerViewModel? = null
) {
    appViewModel?.setAppTitle("Project Opener")
    viewModel?.updateProjectList()

    val projects = if (viewModel != null) {
        val inner by viewModel.projectList.collectAsState()
        inner
    } else {
        listOf(
            ProjectInfo(
                name = "Kotlin",
                description = "preview",
                type = ProjectType.Kotlin,
                path = "null"
            ),
            ProjectInfo(
                name = "Rust",
                description = "preview",
                type = ProjectType.Rust,
                path = "null"
            ),
            ProjectInfo(
                name = "Rust",
                description = "preview",
                type = ProjectType.Rust,
                path = "null"
            ),
            ProjectInfo(
                name = "Rust",
                description = "preview",
                type = ProjectType.Rust,
                path = "null"
            )
        )
    }!!

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            FlowRow(modifier = Modifier.fillMaxSize()) {
                for (project in projects) {
                    ProjectCard(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.3f),
                        title = project.name,
                        onClick = {
                            navController?.navigate(EditorDestination(project))
                        },
                        onLongClick = {
                            viewModel?.setTargetProject(project)
                            viewModel?.setShowBottomSheet(true)
                        },
                        description = project.description,
                        projectType = project.type)
                }
            }

            FloatingActionButton(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 50.dp, bottom = 100.dp),
                onClick = {
                    navController?.navigate(ProjectCreatorDestination)
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

        val showBottomSheet = if (viewModel == null) {
            false
        } else {
            val inner by viewModel.showBottomSheet.collectAsState()
            inner
        }

        if (showBottomSheet) {
            if (viewModel != null) {
                BottomSheet(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    viewModel: ProjectOpenerViewModel,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { viewModel.setShowBottomSheet(false) },
        sheetState = bottomSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val targetProject by viewModel.targetProject.collectAsState()

            ProjectIcon(
                modifier = Modifier.size(100.dp),
                projectType = targetProject!!.type
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier.width(200.dp),
                text = targetProject!!.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier.width(200.dp),
                text = targetProject?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    viewModel.deleteProject(viewModel.targetProject.value!!)
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            viewModel.setShowBottomSheet(false)
                        }
                    }
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Delete", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
