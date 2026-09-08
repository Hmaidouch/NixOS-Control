package com.example.myapplication.ssh

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurityHelper(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_ssh_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveConfig(config: SshConfig) {
        sharedPreferences.edit().apply {
            putString("host", config.host)
            putInt("port", config.port)
            putString("username", config.username)
            putString("password", config.password)
            putString("sudoPassword", config.sudoPassword)
            putBoolean("useSshKey", config.useSshKey)
            putString("privateKeyPath", config.privateKeyPath)
            apply()
        }
    }

    fun getConfig(): SshConfig {
        return SshConfig(
            host = sharedPreferences.getString("host", "") ?: "",
            port = sharedPreferences.getInt("port", 22),
            username = sharedPreferences.getString("username", "") ?: "",
            password = sharedPreferences.getString("password", "") ?: "",
            sudoPassword = sharedPreferences.getString("sudoPassword", "") ?: "",
            useSshKey = sharedPreferences.getBoolean("useSshKey", false),
            privateKeyPath = sharedPreferences.getString("privateKeyPath", "") ?: ""
        )
    }
}
