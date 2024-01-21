package com.example.useronboarding.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    user: FirebaseAuth = FirebaseAuth.getInstance(),
    editProfile: () -> Unit = {},
    signOut: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        user.currentUser?.let { user ->
            user.photoUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(100.dp)
                        .clip(shape = CircleShape),
                )

            } ?: Image(imageVector = Icons.Outlined.Person, contentDescription = "person",
                       modifier = Modifier
                           .padding(16.dp)
                           .size(100.dp)
                           .clip(shape = CircleShape)
            )

            user.displayName?.let { name ->
                Text(text = name)
            }
            user.email?.let { email ->
                Text(text = email)
            }
            user.phoneNumber?.let { phone ->
                Text(text = phone)
            }

            Button(onClick = editProfile) {
                Text(text = "Edit Profile")
            }
            Button(onClick = { signOut() }) {
                Text(text = "Sign Out")
            }

            Button(onClick = {}) {
                Text(text = "Choose Location")

            }

            Spacer(modifier =Modifier.height(16.dp))
            Surface(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(
                    color = androidx.compose.ui.graphics.Color.Gray
                )) {
            }

        }


    }
}