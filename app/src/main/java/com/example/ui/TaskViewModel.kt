package com.example.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

data class PersonalTask(
    val id: Int,
    val description: String,
    val type: String,
    val isDone: Boolean = false
)

class TaskViewModel(private val jwtToken: String) : ViewModel() {
    private var nextId = 0
    private val _tasks = mutableStateListOf<PersonalTask>()
    val tasks: List<PersonalTask> = _tasks

    // Contoh penggunaan jwtToken
    init {
        println("JWT Token is $jwtToken")
        // Kamu bisa pakai token ini untuk load data dari API misalnya
    }

    fun addTask(description: String, type: String) {
        _tasks.add(PersonalTask(id = nextId++, description = description, type = type))
    }

    fun deleteTask(id: Int) {
        _tasks.removeAll { it.id == id }
    }

    fun toggleDone(id: Int) {
        val index = _tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            val task = _tasks[index]
            _tasks[index] = task.copy(isDone = !task.isDone)
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
