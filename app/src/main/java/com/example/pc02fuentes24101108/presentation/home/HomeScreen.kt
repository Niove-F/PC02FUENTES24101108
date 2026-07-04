package com.example.pc02fuentes24101108.presentation.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pc02fuentes24101108.data.remote.FirebaseFirestoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var resultText by remember { mutableStateOf("") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val currencies = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    // Estado dinámico para las tasas (con un fallback local por si la red falla)
    var exchangeRates by remember {
        mutableStateOf(
            mapOf("USD" to 1.0, "EUR" to 0.925, "PEN" to 3.75, "GBP" to 0.79, "JPY" to 155.0)
        )
    }

    // Cargar las tasas desde Firestore al entrar a la pantalla
    LaunchedEffect(Unit) {
        val result = FirebaseFirestoreManager.getExchangeRates()
        if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
            exchangeRates = result.getOrThrow()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conversor de Divisas") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CAMPO: MONTO ---
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- SELECCIÓN: DE (FROM) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fromCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("De") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { fromExpanded = true })
                DropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = { fromCurrency = curr; fromExpanded = false })
                    }
                }
            }

            IconButton(onClick = {
                val temp = fromCurrency
                fromCurrency = toCurrency
                toCurrency = temp
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Invertir")
            }

            // --- SELECCIÓN: A (TO) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = toCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("A") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { toExpanded = true })
                DropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = { toCurrency = curr; toExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BOTÓN CONVERTIR Y ALMACENAR ---
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null) {
                        val rateFrom = exchangeRates[fromCurrency] ?: 1.0
                        val rateTo = exchangeRates[toCurrency] ?: 1.0

                        val amountInUSD = parsedAmount / rateFrom
                        val convertedAmount = amountInUSD * rateTo

                        val formattedResult = String.format("%.2f", convertedAmount)
                        resultText = "$amount $fromCurrency equivalen a $formattedResult $toCurrency"

                        // Lanzamos una corrutina para guardar de forma asíncrona en Firestore
                        scope.launch {
                            val saveResult = FirebaseFirestoreManager.saveConversion(
                                amount = parsedAmount,
                                fromCurrency = fromCurrency,
                                toCurrency = toCurrency,
                                result = convertedAmount
                            )
                            if (saveResult.isFailure) {
                                Toast.makeText(context, "No se guardó el historial en la BD", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        resultText = "Por favor, ingrese un monto válido."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convertir")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (resultText.isNotEmpty()) {
                Text(text = resultText, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}