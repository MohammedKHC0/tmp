package com.shadow3.codroid.composable

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun CodeEditor(
    modifier: Modifier = Modifier,
    editor: CodeEditor,
    state: CodeEditorState
) {
    AndroidView(
        factory = { editor },
        modifier = modifier,
        onRelease = {
            it.release()
        },
        update = {
            it.updateFromState(state)
        }
    )
}
