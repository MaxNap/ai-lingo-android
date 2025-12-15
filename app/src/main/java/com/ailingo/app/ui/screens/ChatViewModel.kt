package com.ailingo.app.ui.screens
// Should be moved to different folder (not in screens)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(userMessage: String) {
        val newList = _messages.value + ChatMessage("user", userMessage)
        _messages.value = newList

        viewModelScope.launch {
            try {
                val response = OpenAIClient.api.sendMessage(
                    ChatRequest(messages = newList)
                )
                val reply = response.choices.first().message
                _messages.value = _messages.value + reply
            } catch (e: Exception) {
                _messages.value = _messages.value +
                        ChatMessage("assistant", "Error: ${e.localizedMessage}")
            }
        }
    }
}