package com.amix.tutor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amix.tutor.data.ChatDao
import com.amix.tutor.data.ChatEntity
import com.amix.tutor.data.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppViewModel @Inject constructor(
    private val settings: SettingsStore,
    dao: ChatDao
) : ViewModel() {

    val language: StateFlow<String> = settings.language

    val chats: StateFlow<List<ChatEntity>> = dao.observeChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setLanguage(lang: String) = settings.setLanguage(lang)

    fun toggleLanguage() = settings.setLanguage(if (language.value == "ru") "en" else "ru")
}
