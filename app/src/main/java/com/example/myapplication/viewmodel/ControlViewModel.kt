package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ConnectionState
import com.example.myapplication.model.CustomAction
import com.example.myapplication.repository.ComputerControlRepository
import com.example.myapplication.ssh.SecurityHelper
import com.example.myapplication.ssh.SshConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ControlViewModel(
    private val repository: ComputerControlRepository,
    private val securityHelper: SecurityHelper
) : ViewModel() {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _sshConfig = MutableStateFlow(securityHelper.getConfig())
    val sshConfig: StateFlow<SshConfig> = _sshConfig

    val actions: StateFlow<List<CustomAction>> = repository.allActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDefaults()
        }
    }

    fun updateConfig(newConfig: SshConfig) {
        _sshConfig.value = newConfig
        securityHelper.saveConfig(newConfig)
    }

    fun addAction(name: String, command: String, icon: String, requiresSudo: Boolean) {
        viewModelScope.launch {
            repository.addAction(CustomAction(name = name, command = command, iconName = icon, requiresSudo = requiresSudo))
        }
    }

    fun updateAction(action: CustomAction) {
        viewModelScope.launch {
            repository.addAction(action) // Room's OnConflictStrategy.REPLACE will handle the update
        }
    }

    fun deleteAction(action: CustomAction) {
        viewModelScope.launch {
            repository.deleteAction(action)
        }
    }

    fun executeAction(action: CustomAction) {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.Connecting
            repository.executeAction(action)
            val result = repository.lastResult.value
            if (result?.isSuccess == true) {
                _connectionState.value = ConnectionState.Connected
            } else {
                _connectionState.value = ConnectionState.Error(result?.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.Connecting
            val result = repository.testConnection()
            if (result.isSuccess) {
                _connectionState.value = ConnectionState.Connected
            } else {
                _connectionState.value = ConnectionState.Error(result.exceptionOrNull()?.message ?: "Connection failed")
            }
        }
    }
}
