package bg.europos_scanner.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bg.europos_scanner.ui.theme.EuroposScannerTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToScanner: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToScanner -> onNavigateToScanner()
            }
        }
    }

    LoginContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Europos Скенер",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Вход в системата",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { onIntent(LoginIntent.UpdateUsername(it)) },
                        label = { Text("Потребителско име") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var passwordVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onIntent(LoginIntent.UpdatePassword(it)) },
                        label = { Text("Парола") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Скрий паролата" else "Покажи паролата"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntent(LoginIntent.ToggleRememberUsername) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.rememberUsername,
                            onCheckedChange = { onIntent(LoginIntent.ToggleRememberUsername) }
                        )
                        Text(
                            text = "Запомни ме",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    state.error?.let { errorText ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Button(
                            onClick = { onIntent(LoginIntent.Login) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = !state.isLoading
                        ) {
                            Text("Вход")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginContentPreview() {
    EuroposScannerTheme {
        LoginContent(
            state = LoginState(),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun LoginContentErrorPreview() {
    EuroposScannerTheme {
        LoginContent(
            state = LoginState(
                username = "admin",
                password = "wrong",
                error = "Невалидно потребителско име или парола"
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun LoginContentLoadingPreview() {
    EuroposScannerTheme {
        LoginContent(
            state = LoginState(
                username = "admin",
                password = "pass",
                isLoading = true
            ),
            onIntent = {}
        )
    }
}
