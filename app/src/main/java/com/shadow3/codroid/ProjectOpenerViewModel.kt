package com.shadow3.codroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shadow3.codroid.data.ProjectInfo
import com.shadow3.codroid.data.ProjectInfoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectOpenerViewModel(private val projectInfoDao: ProjectInfoDao) : ViewModel() {
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    private val _targetProject = MutableStateFlow<ProjectInfo?>(null)
    val targetProject: MutableStateFlow<ProjectInfo?> = _targetProject

    private val _projectList = MutableStateFlow<List<ProjectInfo>>(emptyList())
    val projectList: StateFlow<List<ProjectInfo>?> = _projectList

    fun updateProjectList() {
        viewModelScope.launch {
            projectInfoDao.getAllProjects().collect { projects ->
                _projectList.value = projects
            }
        }
    }

    fun deleteProject(project: ProjectInfo) {
        viewModelScope.launch {
            withContext(viewModelScope.coroutineContext) {
                projectInfoDao.delete(project)
            }
        }
    }

    fun setTargetProject(project: ProjectInfo?) {
        _targetProject.value = project
    }

    fun setShowBottomSheet(show: Boolean) {
        _showBottomSheet.value = show
    }
}

class ProjectOpenerViewModelFactory(private val projectInfoDao: ProjectInfoDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectOpenerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectOpenerViewModel(projectInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}