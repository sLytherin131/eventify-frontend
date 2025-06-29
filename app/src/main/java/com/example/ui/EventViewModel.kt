package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.api.EventWithDetailsResponse  // ← ini betul
import com.example.api.createApiService          // ← ini juga betul

class EventViewModel(private val token: String) : ViewModel() {
    private val _events = mutableStateListOf<EventWithDetailsResponse>()  // ← gunakan EventWithDetailsResponse
    val events: List<EventWithDetailsResponse> get() = _events

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                val api = createApiService(token)
                _events.clear()
                _events.addAll(api.getEvents())
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error: ${e.message}")
            }
        }
    }
}

class EventViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(token) as T
    }
}
