//package com.dreammap.app.viewmodels
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.dreammap.app.data.repositories.ChatRepository
//
//class ChatViewModelFactory(private val chatRepository: ChatRepository) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
//            return ChatViewModel(chatRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

