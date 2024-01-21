package com.example.useronboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MyAppDrawer(
    navController: NavController,
    currentRoute: String,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalDrawerSheet {
        Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
        NavigationDrawerItem(
            label = {Text(text = "Profile")},
            selected = currentRoute == AppDestinations.PROFILE_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.PROFILE_ROUTE)
                scope.launch {
                    drawerState.close()
                }
            }
        )
    }

}