package com.example.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.api.ApiService
import com.example.api.CreateTaskRequest
import com.example.api.createApiService
import com.example.api.TaskResponse

data class PersonalTask(
    val id: Int,
    val adminWhatsapp: String,
    val description: String,
    val taskType: String,
    val createdAt: Long
)

class TaskViewModel(private val jwtToken: String) : ViewModel() {
    private val _tasks = mutableStateListOf<PersonalTask>()
    val tasks: List<PersonalTask> = _tasks

    private val api = createApiService(jwtToken)

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            try {
                val response = api.getTasks()
                _tasks.clear()
                _tasks.addAll(response.map {
                    PersonalTask(it.id, it.adminWhatsapp, it.description, it.taskType, it.createdAt)
                })
            } catch (e: Exception) {
                println("Error fetching tasks: ${e.message}")
            }
        }
    }

    fun addTask(description: String, type: String) {
        viewModelScope.launch {
            try {
                val newTask = api.addTask(CreateTaskRequest(description, type))
                _tasks.add(
                    PersonalTask(
                        id = newTask.id,
                        adminWhatsapp = newTask.adminWhatsapp,
                        description = newTask.description,
                        taskType = newTask.taskType,
                        createdAt = newTask.createdAt
                    )
                )
            } catch (e: Exception) {
                println("Error adding task: ${e.message}")
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            try {
                api.deleteTask(id)
                _tasks.removeAll { it.id == id }
            } catch (e: Exception) {
                println("Error deleting task: ${e.message}")
            }
        }
    }

}

class TaskViewModelFactory(private val jwtToken: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(jwtToken) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
