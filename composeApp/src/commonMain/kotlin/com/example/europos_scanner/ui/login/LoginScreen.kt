package com.example.europos_scanner.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.ui.theme.FeriaAccent
import com.example.europos_scanner.ui.theme.FeriaBackground
import com.example.europos_scanner.ui.theme.FeriaError
import com.example.europos_scanner.ui.theme.FeriaPrimary
import com.example.europos_scanner.ui.theme.FeriaSecondary
import com.example.europos_scanner.ui.theme.GlassButton
import com.example.europos_scanner.ui.theme.GlassCard
import com.example.europos_scanner.ui.theme.GlassTextField

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(FeriaPrimary, FeriaSecondary, FeriaAccent, FeriaBackground)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Europos Scanner",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Вход в системата",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassTextField(
                value = state.username,
                onValueChange = { viewModel.onIntent(LoginIntent.UpdateUsername(it)) },
                label = { Text("Потребителско име") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassTextField(
                value = state.password,
                onValueChange = { viewModel.onIntent(LoginIntent.UpdatePassword(it)) },
                label = { Text("Парола") },
                modifier = Modifier.fillMaxWidth(),
                isPassword = true
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error!!,
                    color = FeriaError,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp).align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                GlassButton(
                    onClick = { viewModel.onIntent(LoginIntent.Login) },
                    text = "Вход",
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isLoading
                )
            }
        }
    }
}
