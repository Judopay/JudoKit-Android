package com.judokit.android.examples.feature.tokenpayments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
@Suppress("LongParameterList", "LongMethod", "MagicNumber", "UnstableCollections")
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
                title = { Text(stringResource(R.string.feature_title_token_payments)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
        ) {
            // How to guide
            Text(
                text = stringResource(R.string.token_guide_heading),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.token_guide_body),
                style = MaterialTheme.typography.bodyMedium,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Card details section
            Text(
                text = stringResource(R.string.token_section_card_details),
                style = MaterialTheme.typography.titleMedium,
            )
            OutlinedTextField(
                value = viewModel.scheme,
                onValueChange = { viewModel.scheme = it.take(2) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_scheme)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.token,
                onValueChange = { viewModel.token = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_token)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.lastFour,
                onValueChange = { viewModel.lastFour = it.take(4) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_last_four)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.securityCode,
                onValueChange = { viewModel.securityCode = it.take(securityCodeLength) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_security_code)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedTextField(
                value = viewModel.cardholderName,
                onValueChange = { viewModel.cardholderName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.label_cardholder_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                enabled = !viewModel.isBusy,
                singleLine = true,
            )
            OutlinedButton(
                onClick = onTokenizeNewCard,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
                Text(stringResource(R.string.create_card_token).uppercase())
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Actions section
            Text(
                text = stringResource(R.string.token_section_actions),
                style = MaterialTheme.typography.titleMedium,
            )
            Button(
                onClick = onTokenPayment,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid && !viewModel.isBusy,
            ) {
                Text(stringResource(R.string.token_payment).uppercase())
            }
            Button(
                onClick = onTokenPreAuth,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = isFormValid && !viewModel.isBusy,
            ) {
                Text(stringResource(R.string.preauth_token_payment).uppercase())
            }
        }
    }
}
