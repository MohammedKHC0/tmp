package com.shadow3.codroid.composable

import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

data class CodeEditorState(
    var text: Content? = null,
) {
    companion object {
        fun fromEditor(editor: CodeEditor): CodeEditorState {
            return CodeEditorState(text = editor.text)
        }
    }
}

fun CodeEditor.updateFromState(state: CodeEditorState) {
    setText(state.text)
}
