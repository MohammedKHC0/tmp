package com.shadow3.codroid.composable

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.shadow3.codroid.R
import com.shadow3.codroid.data.ProjectType

@Composable
fun ProjectIcon(modifier: Modifier = Modifier, projectType: ProjectType) {
    when (projectType) {
        ProjectType.Kotlin -> Icon(
            modifier = modifier,
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_kotlin),
            contentDescription = null,
            tint = Color.Unspecified
        )

        ProjectType.Rust -> Icon(
            modifier = modifier,
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_rust),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}
