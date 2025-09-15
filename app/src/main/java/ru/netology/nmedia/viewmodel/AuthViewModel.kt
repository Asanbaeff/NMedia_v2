package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState


class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    fun authenticate(
        login: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = AuthApi.service.updateUser(login, pass)
                if (response.isSuccessful) {
                    val body = response.body() ?: throw RuntimeException("Empty body")
                    AppAuth.getInstance().setAuth(body.id, body.token)
                    onSuccess()
                } else {
                    onError(RuntimeException("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }



    fun logout() {
        AppAuth.getInstance().removeAuth()
    }

}