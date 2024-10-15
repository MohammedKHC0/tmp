package com.shadow3.codroid

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadow3.codroid.proot.ProotManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private var _setupCompleted = MutableStateFlow(false)
    val setupCompleted: StateFlow<Boolean> = _setupCompleted
    private var _appTitle = MutableStateFlow("")
    val appTitle: StateFlow<String> = _appTitle

    fun setAppTitle(title: String) {
        _appTitle.value = title
    }

    fun setup(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            ProotManager.init(context = context)
            _setupCompleted.value = true
        }
    }
}
