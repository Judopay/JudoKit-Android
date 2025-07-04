package com.judokit.android.examples.feature.tokenpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.judokit.android.examples.R
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.securityCodeLength

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList", "LongMethod", "MagicNumber")
fun TokenPaymentsScreen(
    viewModel: TokenPaymentsViewModel,
    onTokenizeNewCard: () -> Unit,
    onTokenPayment: () -> Unit,
    onTokenPreAuth: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFormValid by remember {
        derivedStateOf { viewModel.scheme.isNotBlank() && viewModel.token.isNotBlank() && viewModel.lastFour.isNotBlank() }
    }
    val securityCodeLength by remember(key1 = viewModel.scheme) {
        mutableIntStateOf(CardNetwork.withIdentifier(viewModel.scheme.toIntOrNull() ?: 0).securityCodeLength)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.feature_title_token_payments)) },
                navigationIcon = { IconButton(onClick = { onClose() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "") } },
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Please fill in the fields below with a tokenized card's details or tokenize a new card below")
            OutlinedTextField(
                value = viewModel.scheme,
                onValueChange = { viewModel.scheme = it.take(2) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Scheme") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.token,
                onValueChange = { viewModel.token = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Token") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.lastFour,
                onValueChange = { viewModel.lastFour = it.take(4) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Last 4 digits") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.securityCode,
                onValueChange = { viewModel.securityCode = it.take(securityCodeLength) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Security code") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.cardholderName,
                onValueChange = { viewModel.cardholderName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cardholder name") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )

            Button(
                onClick = { onTokenizeNewCard() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) { Text("Tokenize a new card".uppercase()) }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onTokenPayment() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid,
            ) {
                Text("Payment".uppercase())
            }
            Button(
                onClick = { onTokenPreAuth() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid,
            ) {
                Text("Pre-auth".uppercase())
            }
        }
    }
}
