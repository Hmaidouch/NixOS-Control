package com.example.myapplication.repository

import com.example.myapplication.model.ActionDao
import com.example.myapplication.model.CustomAction
import com.example.myapplication.ssh.SecurityHelper
import com.example.myapplication.ssh.SshClientManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ComputerControlRepository(
    private val actionDao: ActionDao,
    private val securityHelper: SecurityHelper,
    private val sshClientManager: SshClientManager
) {
    private val _lastResult = MutableStateFlow<Result<String>?>(null)
    val lastResult: StateFlow<Result<String>?> = _lastResult

    val allActions: Flow<List<CustomAction>> = actionDao.getAllActions()

    suspend fun initializeDefaults() {
        val actions = actionDao.getAllActionsList()
        
        // Remove old Network Manager actions
        actions.forEach { action ->
            if (action.name == "Disable Network" || action.name == "Enable Network") {
                actionDao.deleteAction(action)
            }
        }

        if (actionDao.getCount() == 0 || (actionDao.getCount() == 1 && actions.any { it.name == "Shutdown" })) {
            // Add new defaults if they don't exist
            val currentActions = actionDao.getAllActionsList()
            if (currentActions.none { it.name == "Firefox ON" }) {
                actionDao.insertAction(CustomAction(name = "Firefox ON", command = "firefox-net-on", iconName = "Wifi", requiresSudo = true))
            }
            if (currentActions.none { it.name == "Firefox OFF" }) {
                actionDao.insertAction(CustomAction(name = "Firefox OFF", command = "firefox-net-off", iconName = "WifiOff", requiresSudo = true))
            }
            if (currentActions.none { it.name == "Volume 20%" }) {
                actionDao.insertAction(CustomAction(name = "Volume 20%", command = "wpctl set-volume @DEFAULT_AUDIO_SINK@ 20%", iconName = "Refresh", requiresSudo = false))
            }
            if (currentActions.none { it.name == "Test LS" }) {
                actionDao.insertAction(CustomAction(name = "Test LS", command = "ls", iconName = "Terminal", requiresSudo = false))
            }
            if (currentActions.none { it.name == "Shutdown" }) {
                actionDao.insertAction(CustomAction(name = "Shutdown", command = "shutdown -h now", iconName = "Power", requiresSudo = true))
            }
        }
    }

    suspend fun addAction(action: CustomAction) = actionDao.insertAction(action)
    suspend fun deleteAction(action: CustomAction) = actionDao.deleteAction(action)

    suspend fun executeAction(action: CustomAction) {
        val config = securityHelper.getConfig()
        val result = sshClientManager.executeCommand(config, action.command, useSudo = action.requiresSudo)
        _lastResult.value = result
    }

    suspend fun testConnection(): Result<String> {
        val config = securityHelper.getConfig()
        return sshClientManager.executeCommand(config, "whoami")
    }
}
