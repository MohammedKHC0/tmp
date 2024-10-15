package com.shadow3.codroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shadow3.codroid.data.ProjectInfo
import com.shadow3.codroid.data.ProjectInfoDao
import com.shadow3.codroid.data.ProjectType
import com.shadow3.codroid.proot.ProotManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.file.Files
import kotlin.io.path.Path

class ProjectCreatorViewModel(private val projectInfoDao: ProjectInfoDao) : ViewModel() {
    private var _showProjectTypeBottomSheet = MutableStateFlow<Boolean>(false)
    val showProjectTypeBottomSheet: StateFlow<Boolean> = _showProjectTypeBottomSheet

    private var _projectName = MutableStateFlow<String?>(null)
    val projectName: StateFlow<String?> = _projectName

    private val projectPath: String
        get() {
            return ProotManager.projectDirPath.resolve(_projectName.value).toString()
        }

    private var _projectType = MutableStateFlow<ProjectType?>(null)
    val projectType: StateFlow<ProjectType?> = _projectType

    private var _projectDescription = MutableStateFlow<String?>("")
    val projectDescription: StateFlow<String?> = _projectDescription

    private var _nameConflict = MutableStateFlow<Boolean?>(null)
    val nameConflict: StateFlow<Boolean?> = _nameConflict

    fun setProjectType(type: ProjectType) {
        _projectType.value = type
    }

    fun setShowProjectTypeBottomSheet(show: Boolean) {
        _showProjectTypeBottomSheet.value = show
    }

    fun setProjectName(name: String) {
        _projectName.value = name
    }

    fun setProjectDescription(description: String) {
        _projectDescription.value = description
    }

    fun createProject() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val projectInfo = ProjectInfo(
                name = _projectName.value!!,
                description = _projectDescription.value,
                type = _projectType.value!!,
                path = projectPath
            )
            projectInfoDao.insert(projectInfo)
            Files.createDirectory(Path(path = projectPath))
        }
    }

    fun checkNameConflict() {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (_projectName.value != null) _nameConflict.value =
                projectInfoDao.exists(_projectName.value!!)
        }
    }
}

class ProjectCreatorViewModelModelFactory(private val projectInfoDao: ProjectInfoDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectCreatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ProjectCreatorViewModel(projectInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}