package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthApiService
import ru.netology.nmedia.auth.AuthApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val authApi: AuthApiService
) : ViewModel() {
    val data: LiveData<AuthState> = auth.authStateFlow.asLiveData(Dispatchers.Default)


    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

    fun authenticate(
        login: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authApi.updateUser(login, pass)
                if (response.isSuccessful) {
                    val body = response.body() ?: throw RuntimeException("Empty body")
                    auth.setAuth(body.id, body.token)
                    onSuccess()
                } else {
                    onError(RuntimeException("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}