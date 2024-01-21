package com.example.useronboarding.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    auth : FirebaseAuth = FirebaseAuth.getInstance(),
    backToProfile : () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        var name by rememberSaveable() {
            mutableStateOf("")
        }
        OutlinedTextField(value = name, onValueChange = {
            name = it
        }, label = { Text(text = "Name") })



        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                auth.currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Success", "User profile updated.")
                            backToProfile()
                        }
                    }
            }
        }) {
            Text(text = "Save")
        }
    }

}