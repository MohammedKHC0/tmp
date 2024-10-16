package com.shadow3.codroid.composable

import android.content.Context
import android.util.Log
import com.shadow3.codroid.jni.NativeInotifyHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class DirectoryFilesState {
    private val coroutineScope = CoroutineScope(context = Dispatchers.Main)
    private var inited = false

    private val _filesList = MutableStateFlow<List<Path>>(emptyList())
    val filesList: StateFlow<List<Path>> = _filesList

    private var _filesListPath = MutableStateFlow<Path?>(null)
    val filesListPath: StateFlow<Path?> = _filesListPath

    fun setFileListPath(context: Context, path: Path) {
        _filesListPath.value = path
        initWatching(context = context)
        updateFilesList()
    }

    private fun initWatching(context: Context) {
        if (!inited && _filesListPath.value != null) {
            inited = true
            coroutineScope.launch(context = Dispatchers.IO) {
                while (true) {
                    if (NativeInotifyHelper().waitUntilUpdateTimeout(
                            path = _filesListPath.value.toString(),
                            timeoutMillis = 100
                        )
                    ) {
                        updateFilesList()
                    }
                }
            }
        }
    }

    private fun updateFilesList() {
        try {
            _filesList.value =
                Files.walk(_filesListPath.value, 2).skip(1).collect(Collectors.toList())
        } catch (e: UncheckedIOException) {
            Log.e("files_list", e.stackTraceToString())
        }
    }
}
