package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.AppDatabase
import com.example.myapplication.repository.ComputerControlRepository
import com.example.myapplication.ssh.SecurityHelper
import com.example.myapplication.ssh.SshClientManager
import com.example.myapplication.ui.ControlScreen
import com.example.myapplication.ui.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.ControlViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val securityHelper = SecurityHelper(this)
        val sshClientManager = SshClientManager()
        val repository = ComputerControlRepository(database.actionDao(), securityHelper, sshClientManager)
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: ControlViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ControlViewModel(repository, securityHelper) as T
                        }
                    }
                )
                
                var currentScreen by remember { mutableStateOf("control") }
                
                if (currentScreen == "control") {
                    ControlScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { currentScreen = "settings" }
                    )
                } else {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = "control" }
                    )
                }
            }
        }
    }
}

