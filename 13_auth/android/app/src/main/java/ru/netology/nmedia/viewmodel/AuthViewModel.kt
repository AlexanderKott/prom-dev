package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState

class AuthViewModel : ViewModel() {
    //Это чтобы заставить меню перересовываться в зависимости от залогинен/разлогинен
    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    //Это чтобы приложение знало какое меню показывать (залогинен или нет)
    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L
}