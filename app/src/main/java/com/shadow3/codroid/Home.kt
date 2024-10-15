package com.shadow3.codroid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.NavHostParam
import com.ramcosta.composedestinations.generated.destinations.ProjectOpenerDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>(start = true)
fun Home(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    @NavHostParam
    appViewModel: AppViewModel,
) {
    HomeContent(
        modifier = modifier,
        navigator = navigator,
        appViewModel = appViewModel,
    )
}

@Composable
@Preview
fun HomeContent(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator? = null,
    appViewModel: AppViewModel? = null,
) {
    appViewModel?.setAppTitle("Home")

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_code),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Codroid", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(70.dp))

            Column(modifier = Modifier.padding(20.dp)) {
                val cardModifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)

                val setupCompleted = if (appViewModel == null) {
                    true
                } else {
                    val inner by appViewModel.setupCompleted.collectAsState()
                    inner
                }

                MenuCard(
                    modifier = cardModifier,
                    title = "Project",
                    description = "open or create a project",
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.open_in_new_icon),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        navigator?.navigate(ProjectOpenerDestination)
                    },
                    enabled = setupCompleted
                )

                Spacer(modifier = Modifier.height(10.dp))

                MenuCard(
                    modifier = cardModifier,
                    title = "Setting",
                    description = "configure your own codroid",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        /* TODO */
                    },
                    enabled = setupCompleted
                )

                Spacer(modifier = Modifier.height(10.dp))

                MenuCard(
                    modifier = cardModifier,
                    title = "About",
                    description = "author, licenses, etc.",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        /* TODO */
                    },
                    enabled = setupCompleted
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 300, heightDp = 70)
fun MenuCard(
    modifier: Modifier = Modifier,
    title: String = "PreviewCard",
    description: String = "preview",
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
        )
    },
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Card(modifier = modifier, onClick = onClick, enabled = enabled) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            icon()
        }
    }
}
