package com.shadow3.codroid

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.NavHostParam
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.shadow3.codroid.composable.ProjectCard
import com.shadow3.codroid.composable.ProjectIcon
import com.shadow3.codroid.data.ProjectInfoRoomDataBase
import com.shadow3.codroid.data.ProjectType

@Composable
@Destination<RootGraph>
fun ProjectCreator(
    @NavHostParam projectInfoDatabase: ProjectInfoRoomDataBase,
    @NavHostParam appViewModel: AppViewModel,
    navController: DestinationsNavigator,
) {
    ProjectCreatorContent(
        projectInfoDatabase = projectInfoDatabase,
        viewModel = viewModel(factory = ProjectCreatorViewModelModelFactory(projectInfoDao = projectInfoDatabase.projectInfoDao())),
        appViewModel = appViewModel,
        navController = navController,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun ProjectCreatorContent(
    projectInfoDatabase: ProjectInfoRoomDataBase? = null,
    viewModel: ProjectCreatorViewModel? = null,
    appViewModel: AppViewModel? = null,
    navController: DestinationsNavigator? = null,
) {
    appViewModel?.setAppTitle("Project Creator")
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            item {
                val context = LocalContext.current
                val projectName = if (viewModel == null) {
                    null
                } else {
                    val inner by viewModel.projectName.collectAsState()
                    inner
                }

                val projectDescription = if (viewModel == null) {
                    null
                } else {
                    val inner by viewModel.projectDescription.collectAsState()
                    inner
                }

                val projectType = if (viewModel == null) {
                    null
                } else {
                    val inner by viewModel.projectType.collectAsState()
                    inner
                }

                val nameConflict = if (viewModel == null) {
                    false
                } else {
                    val inner by viewModel.nameConflict.collectAsState()
                    inner
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Configure Project", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(50.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel?.setShowProjectTypeBottomSheet(show = true) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Select Project Template",
                            style = MaterialTheme.typography.titleSmall
                        )

                        AnimatedVisibility(visible = projectType != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            ProjectIcon(
                                projectType = projectType!!,
                                modifier = Modifier.size(45.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Click Me!",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel?.setShowProjectTypeBottomSheet(show = true) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Set Project Name", style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Input here",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                            value = projectName ?: "",
                            onValueChange = {
                                viewModel?.setProjectName(name = it)
                                viewModel?.checkNameConflict()
                            },
                            placeholder = {
                                Text(
                                    text = "name",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            textStyle = MaterialTheme.typography.labelLarge,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        AnimatedVisibility(visible = nameConflict ?: false) {
                            Text(
                                text = "name conflict!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel?.setShowProjectTypeBottomSheet(show = true) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Set Project Description",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Input here",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                            value = projectDescription ?: "",
                            onValueChange = {
                                viewModel?.setProjectDescription(description = it)
                                viewModel?.checkNameConflict()
                            },
                            placeholder = {
                                Text(
                                    text = "description",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            textStyle = MaterialTheme.typography.labelLarge,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Preview", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(50.dp))

                val projectTitleColor = if (nameConflict == true || projectName == null) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }

                val projectTitle = if (nameConflict == true) {
                    "Name conflict: project \"${projectName!!}\" exist!"
                } else {
                    projectName ?: "Missing project name"
                }

                val finishAble =
                    viewModel != null && projectType != null && projectName != null && !(nameConflict
                        ?: true)

                val projectCardColor = if (finishAble) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                }

                ProjectCard(
                    modifier = Modifier.height(100.dp),
                    titleColor = projectTitleColor,
                    descriptionColor = MaterialTheme.colorScheme.secondary,
                    title = projectTitle,
                    description = projectDescription ?: "no description yet",
                    projectType = projectType,
                    colors = projectCardColor
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        enabled = finishAble,
                        onClick = {
                            viewModel?.createProject()
                            Toast.makeText(
                                context, "project $projectName created", Toast.LENGTH_LONG
                            ).show()
                            navController?.navigateUp()
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    ) {
                        AnimatedVisibility(visible = finishAble) {
                            Text(text = "Create")
                        }

                        AnimatedVisibility(visible = finishAble.not()) {
                            Text(text = "Uncreatable")
                        }
                    }
                    if (viewModel != null) {
                        val projectTypeSelectorBottomSheetState = rememberModalBottomSheetState()
                        ProjectTypeSelectorBottomSheet(
                            viewModel = viewModel,
                            bottomSheetState = projectTypeSelectorBottomSheetState
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectTypeSelectorBottomSheet(
    viewModel: ProjectCreatorViewModel, bottomSheetState: SheetState,
) {
    val showBottomSheet by viewModel.showProjectTypeBottomSheet.collectAsState()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowProjectTypeBottomSheet(show = false) },
            sheetState = bottomSheetState
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                var searchInput by remember {
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                        )
                    },
                )

                val selectedProjectType by viewModel.projectType.collectAsState()

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProjectType.entries.forEach {
                        AnimatedVisibility(
                            visible = searchInput.trim().isEmpty() || it.toString().lowercase()
                                .contains(searchInput.trim().lowercase())
                        ) {
                            FilterChip(selected = selectedProjectType != null && selectedProjectType == it,
                                onClick = {
                                    viewModel.setProjectType(
                                        type = it
                                    )
                                },
                                label = { Text(text = it.toString()) },
                                leadingIcon = {
                                    if (selectedProjectType != null && selectedProjectType == it) {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}