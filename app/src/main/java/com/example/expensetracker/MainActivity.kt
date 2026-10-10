package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Indigo = Color(0xFF3B2FD1)
private val Ink = Color(0xFF0B0C0F)
private val Body = Color(0xFF5C616B)
private val Soft = Color(0xFFF7F7F8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ExpenseTrackerTheme { LoginScreen() } }
    }
}

@Composable
private fun ExpenseTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Indigo,
            onPrimary = Color.White,
            background = Color.White,
            surface = Color.White,
            onBackground = Ink,
            onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Expense Tracker", color = Indigo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Text("Welcome back", color = Ink, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("See what is due, where it goes, and what is left.", color = Body, fontSize = 16.sp)
            Spacer(Modifier.height(32.dp))

            Text("EMAIL", color = Body, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; submitted = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("you@example.com") },
                shape = RoundedCornerShape(12.dp),
                isError = submitted && email.isBlank()
            )
            if (submitted && email.isBlank()) Text("Enter your email", color = Color(0xFFC92A38), fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))

            Text("PASSWORD", color = Body, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; submitted = false },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter your password") },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Show password")
                    }
                },
                isError = submitted && password.isBlank()
            )
            if (submitted && password.isBlank()) Text("Enter your password", color = Color(0xFFC92A38), fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { }) { Text("Forgot password?", color = Indigo) }
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { submitted = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo)
            ) { Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Body)
                Text("  Secure session", color = Body, fontSize = 14.sp)
            }
        }
    }
}
