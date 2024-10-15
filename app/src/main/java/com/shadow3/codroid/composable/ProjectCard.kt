package com.shadow3.codroid.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadow3.codroid.R
import com.shadow3.codroid.data.ProjectType

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(heightDp = 100, widthDp = 250)
fun ProjectCard(
    modifier: Modifier = Modifier,
    title: String = "Codroid", description: String? = "preview",
    projectType: ProjectType? = ProjectType.Kotlin,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    titleColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    descriptionColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
        colors = colors,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium, color = titleColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = descriptionColor
                )
            }

            AnimatedVisibility(visible = projectType != null) {
                ProjectIcon(projectType = projectType!!, modifier = Modifier.size(45.dp))
            }
        }
    }
}
