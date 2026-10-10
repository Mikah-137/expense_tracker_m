package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
private val IndigoTint = Color(0xFFEFEFFB)
private val Ink = Color(0xFF0B0C0F)
private val Body = Color(0xFF5C616B)
private val Soft = Color(0xFFF7F7F8)
private val Border = Color(0xFFDFE2E6)
private val Green = Color(0xFF0A9B61)
private val Red = Color(0xFFC92A38)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ExpenseTrackerTheme { ExpenseTrackerApp() } }
    }
}

@Composable
private fun ExpenseTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Indigo, onPrimary = Color.White, background = Color.White, surface = Color.White, onBackground = Ink, onSurface = Ink), content = content)
}

@Composable
private fun ExpenseTrackerApp() {
    var loggedIn by remember { mutableStateOf(false) }
    if (loggedIn) DashboardScreen() else LoginScreen { loggedIn = true }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize(), color = Color.White) {
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars).padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("Expense Tracker", color = Indigo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp)); Text("Welcome back", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp)); Text("See what is due, where it goes, and what is left.", color = Body, fontSize = 16.sp)
            Spacer(Modifier.height(32.dp)); Text("EMAIL", color = Body, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp))
            OutlinedTextField(email, { email = it; submitted = false }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("you@example.com") }, shape = RoundedCornerShape(12.dp), isError = submitted && email.isBlank())
            if (submitted && email.isBlank()) Text("Enter your email", color = Red, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp)); Text("PASSWORD", color = Body, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp))
            OutlinedTextField(password, { password = it; submitted = false }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Enter your password") }, shape = RoundedCornerShape(12.dp), visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton({ visible = !visible }) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, if (visible) "Hide password" else "Show password") } }, isError = submitted && password.isBlank())
            if (submitted && password.isBlank()) Text("Enter your password", color = Red, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp)); TextButton({}) { Text("Forgot password?", color = Indigo) }; Spacer(Modifier.height(12.dp))
            Button({ if (email.isNotBlank() && password.isNotBlank()) onLogin() else submitted = true }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Indigo)) { Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Lock, null, tint = Body); Text("  Secure session", color = Body, fontSize = 14.sp) }
        }
    }
}

private data class DueExpense(val name: String, val category: String, val amount: String, val overdue: Boolean)
private val dueExpenses = listOf(DueExpense("Electricity", "Utilities", "₱2,450.00", false), DueExpense("Apartment rent", "Housing", "₱18,000.00", false), DueExpense("Credit card", "Subscriptions", "₱3,280.00", true))

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DashboardScreen() {
    var tab by remember { mutableStateOf(0) }
    val labels = listOf("Dashboard", "Expenses", "Budget", "History")
    Scaffold(
        topBar = { TopAppBar(title = { Text(labels[tab]) }, actions = { IconButton({}) { Icon(Icons.Default.Person, "Profile") } }) },
        bottomBar = { NavigationBar(containerColor = Color.White) { listOf(Icons.Default.Dashboard, Icons.Default.ReceiptLong, Icons.Default.AccountBalanceWallet, Icons.Default.InsertChart).forEachIndexed { i, icon -> NavigationBarItem(tab == i, { tab = i }, { Icon(icon, labels[i]) }, label = { Text(labels[i], fontSize = 12.sp) }) } } },
        floatingActionButton = { if (tab != 3) FloatingActionButton({}, containerColor = Indigo, contentColor = Color.White) { Icon(Icons.Default.Add, "Add expense") } },
        containerColor = Color.White
    ) { pad -> if (tab == 0) DashboardContent(Modifier.padding(pad)) else PlaceholderTab(labels[tab], Modifier.padding(pad)) }
}

@Composable
private fun DashboardContent(modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Spacer(Modifier.height(8.dp)); Text("₱23,730.00 due today", fontSize = 32.sp); Spacer(Modifier.height(6.dp)); Text("Next cutoff is in 4 days · October 15", color = Body, fontSize = 14.sp) }
        item { SectionTitle("Due today", "3 payments"); Spacer(Modifier.height(8.dp)); Card(border = BorderStroke(1.dp, Border), shape = RoundedCornerShape(16.dp)) { Column { dueExpenses.forEachIndexed { i, e -> DueRow(e); if (i < dueExpenses.lastIndex) HorizontalDivider(color = Border) } } } }
        item { SectionTitle("Where it goes", "This month"); Spacer(Modifier.height(8.dp)); Breakdown() }
        item { SectionTitle("Groups & cutoffs", ""); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Cutoff("1st cutoff", "₱31,450", "Paid", Color(0xFFE8F7F0), Green, Modifier.weight(1f)); Cutoff("2nd cutoff", "₱18,280", "Upcoming", IndigoTint, Indigo, Modifier.weight(1f)) } }
        item { Card(colors = CardDefaults.cardColors(containerColor = Soft), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(Icons.Default.Lightbulb, null, tint = Indigo); Column { Text("A small win", fontWeight = FontWeight.Bold); Spacer(Modifier.height(4.dp)); Text("You are spending 12% less on subscriptions this month.", color = Body, fontSize = 14.sp) } } }; Spacer(Modifier.height(16.dp)) }
    }
}

@Composable private fun SectionTitle(title: String, action: String) { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) { Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); if (action.isNotEmpty()) Text(action, color = Body, fontSize = 13.sp) } }

@Composable private fun DueRow(e: DueExpense) { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(40.dp).background(if (e.overdue) Color(0xFFFFEEF0) else IndigoTint, CircleShape), Alignment.Center) { Icon(if (e.overdue) Icons.Default.Warning else Icons.Default.CalendarMonth, null, tint = if (e.overdue) Red else Indigo) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(e.name, fontWeight = FontWeight.SemiBold); Text(if (e.overdue) "${e.category} · Overdue" else e.category, color = if (e.overdue) Red else Body, fontSize = 13.sp) }; Column(horizontalAlignment = Alignment.End) { Text(e.amount, fontWeight = FontWeight.Medium); IconButton({}) { Icon(Icons.Default.CheckCircle, "Mark ${e.name} paid", tint = if (e.overdue) Body else Green) } } } }

@Composable private fun Breakdown() { Card(border = BorderStroke(1.dp, Border), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(100.dp).background(IndigoTint, CircleShape), Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("₱49,730", fontWeight = FontWeight.Bold); Text("total", color = Body, fontSize = 12.sp) } }; Spacer(Modifier.width(20.dp)); Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Legend("Housing", "₱18,000", Indigo); Legend("Utilities", "₱12,450", Green); Legend("Lifestyle", "₱19,280", Color(0xFFE8A317)) } }; Row(Modifier.fillMaxWidth().height(10.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) { Box(Modifier.weight(.36f).fillMaxSize().background(Indigo, RoundedCornerShape(5.dp))); Box(Modifier.weight(.25f).fillMaxSize().background(Green, RoundedCornerShape(5.dp))); Box(Modifier.weight(.39f).fillMaxSize().background(Color(0xFFE8A317), RoundedCornerShape(5.dp))) } } } }

@Composable private fun Legend(label: String, amount: String, color: Color) { Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(8.dp).background(color, CircleShape)); Spacer(Modifier.width(8.dp)); Text(label, color = Body, fontSize = 13.sp, modifier = Modifier.weight(1f)); Text(amount, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) } }

@Composable private fun Cutoff(title: String, amount: String, status: String, tint: Color, color: Color, modifier: Modifier) { Card(colors = CardDefaults.cardColors(containerColor = tint), shape = RoundedCornerShape(16.dp), modifier = modifier) { Column(Modifier.padding(16.dp)) { Text(title, color = Body, fontSize = 13.sp); Spacer(Modifier.height(8.dp)); Text(amount, fontSize = 20.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.TrendingUp, null, tint = color, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text(status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } } }

@Composable private fun PlaceholderTab(title: String, modifier: Modifier) { Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text("This section is planned for the next phase.", color = Body) } }
