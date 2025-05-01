package com.javips02.on

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.javips02.on.ui.theme.ONTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import com.javips02.on.persistence.AppDatabase
import com.javips02.on.persistence.user.User
import kotlinx.coroutines.flow.firstOrNull

class MainActivity() : ComponentActivity() {
    private lateinit var database: AppDatabase

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getInstance(applicationContext)
        Log.d("DatabaseDebug", "MainActivity Database Instance: $database")
        setContent {
            val navController = rememberNavController() // Get the NavController here
            ONTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text("Login / Register") }) }
                ) { padding ->
                    SurfaceContent(padding = padding, database = database, navController = navController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // No need to close the database explicitly with Room.
    }
}

@Composable
fun SurfaceContent(padding: PaddingValues, database: AppDatabase, navController: NavController) {
    var showLogin by rememberSaveable { mutableStateOf(false) }
    var showRegister by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var feedbackMessage by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // You'll need access to the NavController here.
    // One way to do this is to pass it as a parameter to SurfaceContent.
    // val navController = rememberNavController() // Get the NavController here if MainActivity doesn't pass it.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!showLogin && !showRegister) {
            Button(
                onClick = { showLogin = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Login")
            }
            Button(
                onClick = { showRegister = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Register")
            }
        } else if (showLogin) {
            Text("Login", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
            LoginForm(email = email, password = password, onEmailChange = { email = it }, onPasswordChange = { password = it }) {
                coroutineScope.launch {
                    try {
                        val user = database.userDao().getByUsername(email).firstOrNull()
                        if (user != null && user.password == password) { // NEVER compare plain passwords.  Use hashing.
                            feedbackMessage = "Login successful! (Simulated)"
                            showDialog = true
                            // **NAVIGATION ON SUCCESSFUL LOGIN:**
                            //navController.navigate(Screen.Functionscreen.withArgs(user.username)) // Navigate to Functionscreen
                        } else {
                            feedbackMessage = "Invalid email or password"
                            showDialog = true
                        }
                    } catch (e: Exception) {
                        // Log the error for debugging purposes
                        e.printStackTrace()
                        feedbackMessage = "An error occurred during login."
                        showDialog = true
                    }

                }
            }
            Button(onClick = {
                showLogin = false; email = ""; password = "";
            }, modifier = Modifier.padding(8.dp)) {
                Text("Cancel")
            }
        } else if (showRegister) {
            Text("Register", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
            RegisterForm(email = email, username = username, password = password,
                onEmailChange = { email = it },
                onUsernameChange = { username = it },
                onPasswordChange = { password = it }) {
                coroutineScope.launch {
                    // Check if the username is already taken.
                    if (database.userDao().isUsernameTaken(username)) {
                        feedbackMessage = "Username already taken."
                        showDialog = true
                    } else {
                        // Username is available, proceed with registration.
                        val newUser = User(username = username, email = email, password = password)  // NEVER store plain passwords
                        val result = database.userDao().insert(newUser) // Get the insert result
                        if (result > 0) { // Check if insert was successful
                            feedbackMessage = "Registration successful! (Simulated)"
                            showDialog = true
                        } else {
                            feedbackMessage = "Registration failed." // Handle the error.
                            showDialog = true
                        }
                    }
                }
            }
            Button(onClick = {
                showRegister = false; email = ""; username = ""; password = ""
            }, modifier = Modifier.padding(8.dp)) {
                Text("Cancel")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Result") },
                text = { Text(feedbackMessage) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false;
                        if (feedbackMessage.contains("successful")) {
                            showLogin = false;
                            showRegister = false;
                            email = "";
                            username = "";
                            password = "";
                            // **NAVIGATION ON SUCCESSFUL LOGIN:**
                            navController.navigate(Screen.Functionscreen.withArgs("AURAMAXING")) // Navigate to Functionscreen
                        }
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(email: String, password: String, onEmailChange: (String) -> Unit, onPasswordChange: (String) -> Unit, onLogin: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onLogin, modifier = Modifier.padding(8.dp)) {
            Text("Submit")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterForm(email: String, username: String, password: String, onEmailChange: (String) -> Unit, onUsernameChange: (String) -> Unit, onPasswordChange: (String) -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onRegister, modifier = Modifier.padding(8.dp)) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController() // Get the NavController here
    ONTheme {
        SurfaceContent(padding = PaddingValues(0.dp), database = AppDatabase.getInstance(context = TODO()), navController = navController)
    }
}
