package com.example.useronboarding.login

import com.example.useronboarding.login.model.Result
import android.accounts.NetworkErrorException
import android.app.Activity
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.useronboarding.Dependency
import com.example.useronboarding.R
import com.example.useronboarding.login.model.LoggedInUserView
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO: Remove default value for loginRegisterRepository

sealed interface LoginScreenState {

    object Nothing : LoginScreenState
    data class Loading(val isLoading: Boolean = true) : LoginScreenState
    data class Success(val result: LoggedInUserView) : LoginScreenState

    // For Toast-able errors
    data class Error(
        val errorType: ErrorType = ErrorType.BLANK,
        val errorMessages: String = "",
    ) : LoginScreenState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    val otp: MutableState<String> = mutableStateOf("")
    fun onOtpChanged(otp: String) {
        this.otp.value = otp
    }
    val mobileNo: MutableState<String> = mutableStateOf("")

    fun onMobileNoChanged(mobileNo: String) {
        this.mobileNo.value = mobileNo
    }

    private val loginRegisterRepository: LoginRegisterRepository = Dependency.getLoginRepository()

    private var _emailPhone by mutableStateOf("")
    val emailPhone: String
        get() = _emailPhone

    fun onEmailPhoneChanged(username: String) {
        _emailPhone = username
        viewModelScope.launch {

        }
    }


    private var _inputPassword by mutableStateOf("")
    val inputPassword: String
        get() = _inputPassword

    fun onPasswordChange(password: String) {
        _inputPassword = password
        viewModelScope.launch {

        }
    }

    private val _loginForm = MutableStateFlow<LoginFormState>(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginForm

//    private val _registerLoginResult = MutableStateFlow<>()
//    val registerLoginResult: StateFlow<RegisterLoginResult> = _registerLoginResult

    private val _loginScreenState = MutableStateFlow<LoginScreenState>(LoginScreenState.Nothing)
    val loginScreenState: StateFlow<LoginScreenState> = _loginScreenState

//    private val _loginScreenEvent = MutableStateFlow<LoginScreenEvent>(LoginScreenEvent.Idle)
//    val loginScreenEvent: StateFlow<LoginScreenEvent> = _loginScreenEvent
//

    fun onLoginClicked() {
        if (validateInputData()) {
            login(emailPhone, inputPassword)
        }
    }

    private fun validateInputData(): Boolean {
        val isEmailValid = isEmailValid(emailPhone).also {
            _loginForm.value = _loginForm.value.copy(emailError = it)
        }.let { errorType ->
            errorType == ErrorType.BLANK
        }
        val isPasswordValid = isPasswordValid(inputPassword).also {
            _loginForm.value = _loginForm.value.copy(passwordError = it)
        }.let { errorType ->
            errorType == ErrorType.BLANK
        }

        return isEmailValid && isPasswordValid
    }


    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            loginRegisterRepository.login(username, password).collect { result ->
                handleResult(result)
            }
        }

    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            loginRegisterRepository.loginWithGoogle(idToken).collect { result ->

            }
        }

    }

    private fun handleResult(result: Result<LoggedInUser>) {
        when (result) {
            is Result.Success -> {
                _loginScreenState.value = LoginScreenState.Success(
                    LoggedInUserView(
                        displayName = result.data.displayName,
                        email = result.data.userId,
                    )
                )
            }

            is Result.Error -> {
                handleLoginException(result.exception)
            }

            is Result.Loading -> {
                _loginScreenState.value = LoginScreenState.Loading(result.loadingStatus)
            }
        }
    }

    private fun handleLoginException(exception: Exception) {
        when (exception) {
            is NullPointerException -> {
                _loginForm.value = _loginForm.value.copy(emailError = ErrorType.EMAIL_NOT_FOUND)
            }

            is FirebaseAuthInvalidCredentialsException -> {
                _loginForm.value =
                        _loginForm.value.copy(passwordError = ErrorType.INVALID_CREDENTIALS)
            }

            is FirebaseAuthInvalidUserException -> {
                _loginForm.value = _loginForm.value.copy(emailError = ErrorType.EMAIL_NOT_FOUND)
            }

            is FirebaseAuthException -> {
                _loginForm.value =
                        _loginForm.value.copy(passwordError = ErrorType.INVALID_CREDENTIALS)
            }

            // Default case for all other exceptions
            is NetworkErrorException -> {
                _loginScreenState.value =
                        LoginScreenState.Error(errorType = ErrorType.NETWORK_ERROR)
            }

            else -> {
                _loginScreenState.value =
                        LoginScreenState.Error(errorMessages = exception.localizedMessage ?: "")
            }
        }
    }

    fun createUserWithPhone(mobile: String, activity: Activity): Flow<Result<String>> {
        return loginRegisterRepository.createUserWithPhone(mobile, activity)

    }

    fun signInWithCredential(otp: String): Flow<Result<String>> {
        return loginRegisterRepository.signWithCredential(otp)
    }
}
data class LoginFormState(
    val emailError: ErrorType = ErrorType.BLANK,
    val passwordError: ErrorType = ErrorType.BLANK,
) {

    fun isEmailError(): Boolean {
        return emailError != ErrorType.BLANK
    }

    fun isPasswordError(): Boolean {
        return passwordError != ErrorType.BLANK
    }
}
enum class ErrorType(
    @StringRes
    val resourceId: Int,
) {
    /**
     * General error case
     * */
    BLANK(R.string.blank),
    OTHER(R.string.some_error_occurred ),
    EMPTY_FIELD(R.string.empty_email),

    /**
     * Email related errors
     * */

    INVALID_EMAIL(R.string.invalid_email),
    EMAIL_NOT_FOUND(R.string.email_not_found),
    EMAIL_ALREADY_REGISTERED(R.string.email_already_register),

    /**
     * Password related errors
     * */
    INCORRECT_PASSWORD(R.string.incorrect_password),
    SHORT_PASSWORD(R.string.short_password),
    INVALID_CREDENTIALS(R.string.invalid_credentials),
    PASSWORD_MISMATCH(R.string.password_mismatch),


    /**
     * Network related errors
     * */
    NETWORK_ERROR(R.string.network_error),



}
fun isEmailValid(string: String): ErrorType {
    return Patterns.EMAIL_ADDRESS.matcher(string).matches().let { isMatched ->
        if (!isMatched) {
            ErrorType.INVALID_EMAIL
        } else {
            ErrorType.BLANK
        }
    }
}

fun isPasswordValid(password: String): ErrorType {
    return if (password.length < 8) {
        ErrorType.SHORT_PASSWORD
    } else {
        ErrorType.BLANK
    }
}

