package com.shadow3.codroid

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shadow3.codroid.composable.CodeEditorState
import com.shadow3.codroid.data.ProjectInfo
import com.shadow3.codroid.proot.ProotManager
import io.flutter.plugin.common.MethodChannel
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText

class EditorViewModel(private val projectInfo: ProjectInfo) : ViewModel() {
    private var prootInited = false

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    private val _controlState = MutableStateFlow(false)
    val controlState: StateFlow<Boolean> = _controlState

    private val _altState = MutableStateFlow(false)
    val altState: StateFlow<Boolean> = _altState

    private val _leftFilesListPath = MutableStateFlow(Path(path = projectInfo.path))
    val leftFilesListPath: StateFlow<Path> = _leftFilesListPath

    private val _rightFilesListPath = MutableStateFlow(Path(path = projectInfo.path))
    val rightFilesListPath: StateFlow<Path> = _rightFilesListPath

    @SuppressLint("StaticFieldLeak")
    var editor: CodeEditor? = null

    private var _currentOpenedFile: MutableStateFlow<Path?> = MutableStateFlow(null)
    val currentOpenedFile: StateFlow<Path?> = _currentOpenedFile

    private var _editorStates: MutableStateFlow<HashMap<Path, CodeEditorState>> =
        MutableStateFlow(HashMap())
    val editorStates: StateFlow<HashMap<Path, CodeEditorState>> = _editorStates

    var editorState by mutableStateOf(
        CodeEditorState()
    )

    fun initEditor(context: Context) {
        editor = CodeEditor(context)
    }

    fun setShowBottomSheet(show: Boolean) {
        _showBottomSheet.value = show
    }

    fun prootLogin(methodChannel: MethodChannel) {
        if (!prootInited) {
            viewModelScope.launch {
                ProotManager.loginToProot { command ->
                    FlutterTerminalMethodChannel.write(methodChannel = methodChannel, cmd = command)
                }
                FlutterTerminalMethodChannel.clear(methodChannel = methodChannel)
            }

            prootInited = true
        }
    }

    fun inputKey(
        methodChannel: MethodChannel,
        key: InputKeys,
    ) {
        viewModelScope.launch {
            FlutterTerminalMethodChannel.inputKey(
                methodChannel = methodChannel,
                key = key,
            )
        }
    }

    fun shiftControlState(methodChannel: MethodChannel) {
        viewModelScope.launch {
            _controlState.value = _controlState.value.not()
            FlutterTerminalMethodChannel.setControlState(
                methodChannel = methodChannel,
                state = _controlState.value
            )
        }
    }

    fun shiftAltState(methodChannel: MethodChannel) {
        viewModelScope.launch {
            _altState.value = _altState.value.not()
            FlutterTerminalMethodChannel.setControlState(
                methodChannel = methodChannel,
                state = _altState.value
            )
        }
    }

    fun openFile(path: Path) {
        if (currentOpenedFile == path) {
            return
        } else if (_currentOpenedFile.value != null) {
            _editorStates.value[currentOpenedFile.value!!] = editorState
        }

        _editorStates.value[path]?.let {
            editorState = it
        } ?: run {
            val text = path.readText(charset = Charsets.UTF_8)
            val state = CodeEditorState(text = Content(text))
            _editorStates.value[path] = state
            editorState = state
        }

        _currentOpenedFile.value = path
    }

    fun setLeftFilesPath(path: Path) {
        if (path.normalize().startsWith(Path(path = projectInfo.path).normalize()))
            _leftFilesListPath.value = path
    }

    fun setRightFilesPath(path: Path) {
        if (path.normalize().startsWith(Path(path = projectInfo.path).normalize())) {
            Log.d("file list", "open dir")
            _rightFilesListPath.value = path
        }
    }
}

class EditorViewModelFactory(private val projectInfo: ProjectInfo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(projectInfo = projectInfo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
