package com.example.useronboarding.phoneauth

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.useronboarding.login.LoginViewModel
import com.example.useronboarding.login.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhoneAuthScreen(
    activity: Activity,
    viewModal: LoginViewModel,
    onLoginSuccess: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val mobile = viewModal.mobileNo.value
    val otp = viewModal.otp.value
    val showOtp = remember() {

        mutableStateOf(false)
    }

    Column(modifier = androidx.compose.ui.Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
           horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(value = mobile,
                          onValueChange = viewModal::onMobileNoChanged,
                          label = { Text("+91") },
                          modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            scope.launch(Dispatchers.Main) {
                viewModal.createUserWithPhone(
                    mobile,
                    activity
                ).collect {
                    when (it) {
                        is Result.Success -> {
                            showOtp.value = true

                        }

                        is Result.Error -> {
                            // show error
                        }

                        is Result.Loading -> {
                            // TODO: Show loading
                        }
                    }
                }
            }
        }) {
            Text(text = "Submit")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Enter Otp")
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(value = otp,
                          onValueChange = viewModal::onOtpChanged,
                          modifier = Modifier.fillMaxWidth(),

                          keyboardOptions = KeyboardOptions.Default.copy(
                              keyboardType = KeyboardType.Number
                          )
        )

        Spacer(modifier = Modifier.height(20.dp))
        if (showOtp.value)
            Button(onClick = {
                scope.launch(Dispatchers.Main) {
                    viewModal.signInWithCredential(
                        otp
                    ).collect {
                        when (it) {
                            is Result.Success<String> -> {
                                onLoginSuccess()
                            }

                            is Result.Error -> {
                            }

                            is Result.Loading -> {
                            }
                        }
                    }
                }
            }) {
                Text(text = "Verify")
            }


    }

}