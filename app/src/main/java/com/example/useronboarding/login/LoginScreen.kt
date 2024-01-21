package com.example.useronboarding.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.useronboarding.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    requestPassword: () -> Unit,
    navigateToRegister: () -> Unit = {},
    onSuccessfulLogin: () -> Unit = {},
    signInWithPhone : () -> Unit,
) {
    val emailPhone = loginViewModel.emailPhone
    val inputPassword = loginViewModel.inputPassword
    val loginFormState by loginViewModel.loginFormState.collectAsStateWithLifecycle()
    val result by loginViewModel.loginScreenState.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        when (result) {
            is LoginScreenState.Loading -> {
                // Show progress bar
            }

            is LoginScreenState.Success -> {
                onSuccessfulLogin()
            }

            is LoginScreenState.Error -> {
                // Show toast
            }

            is LoginScreenState.Nothing -> {
                // Do nothing
            }
        }
    }


    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000)
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(50.dp))
        LoginAppHeader()
        LoginBody(
            emailPhone = emailPhone,
            onEmailPhoneChanged = loginViewModel::onEmailPhoneChanged,
            inputPassword = inputPassword,
            onPasswordChange = loginViewModel::onPasswordChange,
            loginScreenState = loginFormState,
            onLoginClick = loginViewModel::onLoginClicked,
            onNavigateToRegisterClicked = {
                navigateToRegister()
            },
            requestPassword = requestPassword,
            signInWithPhone = signInWithPhone,
        )
    }
}

@Composable
fun LoginAppHeader() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "image description",
        contentScale = ContentScale.None,
        modifier = Modifier
            .width(92.dp)
            .height(92.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = stringResource(id = R.string.app_name), style = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000),
        )
    )
    Spacer(modifier = Modifier.height(18.dp))

}

@Composable
private fun LoginBody(
    emailPhone: String,
    onEmailPhoneChanged: (String) -> Unit,
    inputPassword: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit = {},
    loginScreenState: LoginFormState,
    onNavigateToRegisterClicked: () -> Unit,
    requestPassword: () -> Unit,
    signInWithPhone : () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(.80F)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(text = "Login to your account", style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF09051C),
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,))

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier,
            value = emailPhone, singleLine = true,
            onValueChange = {
                onEmailPhoneChanged(it)

            },
            label = {
                InputFieldLabel(
                    text = "Email", error = loginScreenState.isEmailError()
                )
            },
            isError = loginScreenState.isEmailError(),
            supportingText = {
                if (loginScreenState.isEmailError()) {
                    InputFieldSupportingText(
                        textRes = loginScreenState.emailError.resourceId, isError = true
                    )
                }
            },
            shape = RoundedCornerShape(size = 15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF53E88B),

                ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = inputPassword, singleLine = true, onValueChange = {
            onPasswordChange(it)

        }, visualTransformation = PasswordVisualTransformation(), label = {
            InputFieldLabel(
                text = "Password", error = loginScreenState.isPasswordError()
            )
        }, isError = loginScreenState.isPasswordError(), supportingText = {
            if (loginScreenState.isPasswordError()) {
                InputFieldSupportingText(
                    textRes = loginScreenState.passwordError.resourceId, isError = true
                )
            }
        }, shape = RoundedCornerShape(size = 15.dp), colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF53E88B),

            )

        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Or\nContinue With", style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF09051C),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
            )
        )
        Button(onClick = {
            signInWithPhone()}) {
           Text(text ="Sign in with Phone")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLoginClick, modifier = Modifier
                .width(160.dp)
                .height(64.dp)
        ) {
            InputFieldLabel(text = "Login", color = Color(0xFFFFFFFF))
        }
        Spacer(modifier = Modifier.height(32.dp))

        InputFieldLabel(
            text = "Don't Have Account?",
            color = Color(0xFF53E88B),
            modifier = Modifier.clickable {
                onNavigateToRegisterClicked()
            })

    }

}
@Composable
fun InputFieldLabel(
    modifier: Modifier = Modifier,
    text: String,
    error: Boolean = false,
    color: Color = Color(0xFF3B3B3B)
) {
    Text(
        modifier = modifier, text = text, style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = if (error) Color(0xFFFF0000) else color,
            letterSpacing = 0.5.sp,
        )
    )
}
@Composable
fun InputFieldSupportingText(textRes: Int, isError: Boolean = false) {
    Text(
        text = stringResource(id = textRes), style = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight(400),
            color = if (isError) Color(0xFFFF0000) else Color(0xFF3B3B3B),
            letterSpacing = 0.5.sp,
        )
    )
}