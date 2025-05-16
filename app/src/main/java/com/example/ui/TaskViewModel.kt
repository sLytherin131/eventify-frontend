package com.example.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class PersonalTask(
    val id: Int,
    val description: String,
    val type: String,
    val isDone: Boolean = false
)

class TaskViewModel : ViewModel() {
    private var nextId = 0
    private val _tasks = mutableStateListOf<PersonalTask>()
    val tasks: List<PersonalTask> = _tasks

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
