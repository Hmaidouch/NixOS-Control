package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.CustomAction
import com.example.myapplication.ssh.SshConfig
import com.example.myapplication.viewmodel.ControlViewModel
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ControlViewModel, onNavigateBack: () -> Unit) {
    val config by viewModel.sshConfig.collectAsState()
    val actions by viewModel.actions.collectAsState()
    val deviceIp = remember { getLocalIpAddress() }

    var host by remember { mutableStateOf(config.host) }
    var port by remember { mutableStateOf(config.port.toString()) }
    var username by remember { mutableStateOf(config.username) }
    var password by remember { mutableStateOf(config.password) }
    var sudoPassword by remember { mutableStateOf(config.sudoPassword) }
    var sameAsSsh by remember { mutableStateOf(false) }

    // State for managing actions (Add/Edit)
    var editingAction by remember { mutableStateOf<CustomAction?>(null) }
    var actionName by remember { mutableStateOf("") }
    var actionCommand by remember { mutableStateOf("") }
    var actionIcon by remember { mutableStateOf("Terminal") }
    var actionSudo by remember { mutableStateOf(true) }

    // Reset fields when editingAction changes
    LaunchedEffect(editingAction) {
        if (editingAction != null) {
            actionName = editingAction!!.name
            actionCommand = editingAction!!.command
            actionIcon = editingAction!!.iconName
            actionSudo = editingAction!!.requiresSudo
        } else {
            actionName = ""
            actionCommand = ""
            actionIcon = "Terminal"
            actionSudo = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // Handles the keyboard correctly
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Phone IP: $deviceIp", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text("Connection Settings", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = host, onValueChange = { host = it }, label = { Text("Host") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = port, onValueChange = { port = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("SSH Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = sameAsSsh, onCheckedChange = { 
                    sameAsSsh = it
                    if (it) sudoPassword = password
                })
                Text("Sudo password same as SSH")
            }
            
            OutlinedTextField(
                value = sudoPassword,
                onValueChange = { sudoPassword = it },
                label = { Text("Sudo Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !sameAsSsh
            )

            Button(onClick = {
                viewModel.updateConfig(SshConfig(host, port.toIntOrNull() ?: 22, username, password, sudoPassword))
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Connection")
            }

            HorizontalDivider()

            Text("Manage Buttons", style = MaterialTheme.typography.titleMedium)
            
            // Add/Edit action form
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (editingAction == null) "Add New Button" else "Edit Button", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(value = actionName, onValueChange = { actionName = it }, label = { Text("Button Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = actionCommand, onValueChange = { actionCommand = it }, label = { Text("Shell Command") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Icon: ")
                        val icons = listOf("Terminal", "Power", "Wifi", "Lock", "Refresh")
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { expanded = true }) { Text(actionIcon) }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                icons.forEach { icon ->
                                    DropdownMenuItem(text = { Text(icon) }, onClick = { 
                                        actionIcon = icon
                                        expanded = false
                                    })
                                }
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Checkbox(checked = actionSudo, onCheckedChange = { actionSudo = it })
                        Text("Use Sudo")
                    }

                    Row(modifier = Modifier.align(Alignment.End)) {
                        if (editingAction != null) {
                            TextButton(onClick = { editingAction = null }) {
                                Text("Cancel")
                            }
                        }
                        Button(
                            onClick = {
                                if (actionName.isNotBlank() && actionCommand.isNotBlank()) {
                                    if (editingAction == null) {
                                        viewModel.addAction(actionName, actionCommand, actionIcon, actionSudo)
                                    } else {
                                        viewModel.updateAction(editingAction!!.copy(
                                            name = actionName,
                                            command = actionCommand,
                                            iconName = actionIcon,
                                            requiresSudo = actionSudo
                                        ))
                                        editingAction = null
                                    }
                                    actionName = ""
                                    actionCommand = ""
                                }
                            }
                        ) {
                            Icon(if (editingAction == null) Icons.Default.Add else Icons.Default.Edit, null)
                            Spacer(Modifier.width(4.dp))
                            Text(if (editingAction == null) "Add Button" else "Update Button")
                        }
                    }
                }
            }

            // List existing actions
            actions.forEach { action ->
                ListItem(
                    headlineContent = { Text(action.name) },
                    supportingContent = { Text(action.command) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { editingAction = action }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteAction(action) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
            }
        }
    }
}

fun getLocalIpAddress(): String {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress ?: continue
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (isIPv4) return sAddr
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return "Unknown"
}
