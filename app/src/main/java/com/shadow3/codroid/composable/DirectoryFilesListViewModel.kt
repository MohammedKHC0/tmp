package com.shadow3.codroid.composable

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadow3.codroid.jni.NativeInotifyHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.UncheckedIOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path

class DirectoryFilesListViewModel : ViewModel() {
    private var inited = false
    private val _fileList = MutableStateFlow<List<Path>>(emptyList())
    val fileList: StateFlow<List<Path>> = _fileList

    fun initWatching(context: Context, path: String) {
        if (!inited) {
            inited = true
            viewModelScope.launch(context = Dispatchers.IO) {
                while (true) {
                    try {
                        _fileList.value =
                            Files.walk(Path(path), 2).skip(1).collect(Collectors.toList())
                    } catch (e: UncheckedIOException) {
                        Log.e("files_list", e.stackTraceToString())
                    }
                    NativeInotifyHelper().waitUntilUpdate(path = path)
                }
            }
        }
    }
}
