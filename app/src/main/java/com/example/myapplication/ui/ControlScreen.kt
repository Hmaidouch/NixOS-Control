package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.ConnectionState
import com.example.myapplication.model.CustomAction
import com.example.myapplication.viewmodel.ControlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(viewModel: ControlViewModel, onNavigateToSettings: () -> Unit) {
    val connectionState by viewModel.connectionState.collectAsState()
    val actions by viewModel.actions.collectAsState()
    var actionToConfirm by remember { mutableStateOf<CustomAction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NixOS Control") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectionStatus(connectionState)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(actions) { action ->
                    ActionButton(
                        title = action.name,
                        icon = getIconForName(action.iconName),
                        onClick = {
                            if (action.name.lowercase().contains("shutdown")) {
                                actionToConfirm = action
                            } else {
                                viewModel.executeAction(action)
                            }
                        },
                        enabled = connectionState !is ConnectionState.Connecting
                    )
                }
            }

            if (connectionState is ConnectionState.Error) {
                Text(
                    text = (connectionState as ConnectionState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    actionToConfirm?.let { action ->
        AlertDialog(
            onDismissRequest = { actionToConfirm = null },
            title = { Text("${action.name}?") },
            text = { Text("Are you sure you want to execute this action?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.executeAction(action)
                    actionToConfirm = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { actionToConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Power" -> Icons.Default.PowerSettingsNew
        "Wifi" -> Icons.Default.SignalWifiStatusbar4Bar
        "WifiOff" -> Icons.Default.SignalWifiOff
        "Terminal" -> Icons.Default.Terminal
        "Lock" -> Icons.Default.Lock
        "Refresh" -> Icons.Default.Refresh
        else -> Icons.Default.Code
    }
}

@Composable
fun ConnectionStatus(state: ConnectionState) {
    val statusText = when (state) {
        is ConnectionState.Idle -> "Disconnected"
        is ConnectionState.Connecting -> "Connecting..."
        is ConnectionState.Connected -> "Connected"
        is ConnectionState.Error -> "Error"
    }
    Text(
        text = "Status: $statusText",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun ActionButton(title: String, icon: ImageVector, onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Slightly shorter for grid
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(
                title, 
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}
