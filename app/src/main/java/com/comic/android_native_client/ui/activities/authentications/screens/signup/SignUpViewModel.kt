package com.comic.android_native_client.ui.activities.authentications.screens.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.comic.android_native_client.constants.Screen
import com.comic.android_native_client.network.dto.request.RegisterRequest
import com.comic.android_native_client.network.services.AuthService
import com.comic.validation_text_field.ValidableTextFieldState
import com.comic.validation_text_field.ValidableTextFieldWatcher
import com.comic.validation_text_field.validator.LengthValidator
import com.comic.validation_text_field.validator.MatchPasswordValidator
import com.comic.validation_text_field.validator.PasswordValidator
import com.comic.validation_text_field.validator.RequiredValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val authService: AuthService
) : ViewModel() {
    var _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading.value
    private val requiredValidator = RequiredValidator()
    private val lengthValidator = LengthValidator(minLength = 3, maxLength = 50)
    val usernameState = ValidableTextFieldState(
        fieldName = "User Name",
        validators = listOf(requiredValidator, lengthValidator),
    )
    val nameState = ValidableTextFieldState(
        fieldName = "Name",
        validators = listOf(requiredValidator, lengthValidator),
    )
    val passwordState = ValidableTextFieldState(
        fieldName = "Password",
        validators = listOf(requiredValidator, PasswordValidator()),
    )
    val matchPasswordState = ValidableTextFieldState(
        fieldName = "Match Passowrd",
        validators = listOf(MatchPasswordValidator(passwordState)),
    )

    init {
        passwordState.dependentStates += matchPasswordState
    }

    private val fieldWatcher = ValidableTextFieldWatcher(usernameState, passwordState, nameState)

    fun validateFields(): Boolean {
        return fieldWatcher.validateAll()
    }

    fun signUp(navController: NavController) {
        if (validateFields()) {
            _isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = authService.register(
                    RegisterRequest(
                        username = usernameState.value,
                        password = passwordState.value,
                        name = nameState.value
                    )
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        navController.navigate(Screen.Login)
                        val msg = "Registration successful"

                        Toast.makeText(
                            context,
                            msg,
                            msg.length
                        ).show()
                    }

                } else {
                    when (response.code()) {
                        409 -> {
                            withContext(Dispatchers.Main) {
                                val msg = "Username or password existed"
                                Toast.makeText(
                                    context,
                                    msg,
                                    msg.length
                                ).show()
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                val msg = "Something went wrong" + response.code()
                                Toast.makeText(
                                    context,
                                    msg,
                                    msg.length
                                ).show()
                            }
                        }
                    }
                }
                _isLoading.value = false
            }

        }
    }
}

