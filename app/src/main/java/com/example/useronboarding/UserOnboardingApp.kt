package com.example.useronboarding

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.useronboarding.login.LoginScreen
import com.example.useronboarding.login.LoginViewModel
import com.example.useronboarding.phoneauth.PhoneAuthScreen
import com.example.useronboarding.profile.EditProfileScreen
import com.example.useronboarding.profile.ProfileScreen
import com.example.useronboarding.ui.theme.UserOnboardingTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOnboardingApp(
    activity: Activity
) {
    UserOnboardingTheme {

        val navController = rememberNavController()
        val auth = FirebaseAuth.getInstance()

        var showAppBar by remember {
            mutableStateOf(true)
        }

        DisposableEffect(key1 = auth) {
            val authStateListener = FirebaseAuth.AuthStateListener {
                val user = it.currentUser
                if (user == null) {
                    navController.popBackStack(AppDestinations.LOGIN_ROUTE, true)
                    navController.navigate(AppDestinations.LOGIN_ROUTE)
                }
            }
            auth.addAuthStateListener(authStateListener)

            onDispose {
                auth.removeAuthStateListener(authStateListener)
            }
        }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        showAppBar = when (currentBackStackEntry?.destination?.route) {
            AppDestinations.LOGIN_ROUTE -> {
                false
            }

            AppDestinations.REGISTER_ROUTE -> {
                false
            }

            else -> {
                true
            }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                MyAppDrawer(
                    navController = navController,
                    currentRoute = navController.currentDestination?.route ?: "",
                    drawerState = drawerState,
                    scope = scope
                )
            },
        ) {
            Scaffold(topBar = {
                if (showAppBar) {
                    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
                              navigationIcon = {
                                  IconButton(onClick = {
                                      scope.launch {
                                          drawerState.open()
                                      }
                                  }) {
                                      Icon(imageVector = Icons.Default.Menu,
                                           contentDescription = "Menu")
                                  }
                              })
                }
            }) { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.PROFILE_ROUTE,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(AppDestinations.LOGIN_ROUTE) {
                        val loginViewModel = viewModel<LoginViewModel>()
                        LoginScreen(loginViewModel = loginViewModel,
                                    requestPassword = { /*TODO*/ },
                                    onSuccessfulLogin = {
                                        navController.navigate(AppDestinations.PROFILE_ROUTE)
                                    },
                                    signInWithPhone = {
                                        navController.navigate(AppDestinations.PHONE_AUTH_ROUTE)
                                    })
                    }

                    composable(AppDestinations.PHONE_AUTH_ROUTE) {
                        val loginViewModel = viewModel<LoginViewModel>()
                        PhoneAuthScreen(
                            activity = activity,
                            viewModal = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate(AppDestinations.PROFILE_ROUTE)
                            }
                        )
                    }
                    composable(AppDestinations.PROFILE_ROUTE) {
                        ProfileScreen(
                            editProfile = {
                                navController.navigate(AppDestinations.EDIT_PROFILE_ROUTE)
                            },
                            signOut = {
                                auth.signOut()
                            }
                        )
                    }
                    composable(AppDestinations.EDIT_PROFILE_ROUTE) {
                        EditProfileScreen(
                            backToProfile = {
                                navController.popBackStack()
                            }
                        )
                    }
                }

            }

        }
    }
}

object AppDestinations {

    const val EDIT_PROFILE_ROUTE: String = "edit_profile"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val PROFILE_ROUTE = "profile"
    const val PHONE_AUTH_ROUTE = "phone_auth"
}
