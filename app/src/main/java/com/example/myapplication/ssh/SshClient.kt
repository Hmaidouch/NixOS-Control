package com.example.myapplication.ssh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.IOException
import java.security.Security

class SshClientManager {

    init {
        setupBouncyCastle()
    }

    private fun setupBouncyCastle() {
        // Remove existing BC if any to avoid conflicts
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    suspend fun executeCommand(config: SshConfig, command: String, useSudo: Boolean = false): Result<String> = withContext(Dispatchers.IO) {
        val client = SSHClient()
        try {
            val resolvedHost = if (config.host.endsWith(".local")) {
                // محاولة حل العنوان يدوياً أو استخدام الـ IP المباشر إذا فشل
                try {
                    java.net.InetAddress.getByName(config.host).hostAddress ?: config.host
                } catch (e: Exception) {
                    config.host
                }
            } else {
                config.host
            }

            client.addHostKeyVerifier(PromiscuousVerifier())
            client.connectTimeout = 5000 
            client.timeout = 10000 
            client.connect(resolvedHost, config.port)
            
            if (config.useSshKey && config.privateKeyPath.isNotEmpty()) {
                client.authPublickey(config.username, config.privateKeyPath)
            } else {
                client.authPassword(config.username, config.password)
            }

            val finalCommand = if (useSudo) {
                // Using -S to read password from stdin
                "echo '${config.sudoPassword}' | sudo -S $command"
            } else {
                command
            }

            client.startSession().use { session ->
                val cmd = session.exec(finalCommand)
                val output = cmd.inputStream.bufferedReader().readText()
                val error = cmd.errorStream.bufferedReader().readText()
                cmd.join()
                
                if (cmd.exitStatus == 0) {
                    Result.success(output)
                } else {
                    Result.failure(IOException("Command failed with exit code ${cmd.exitStatus}: $error"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try {
                client.disconnect()
            } catch (e: Exception) {
                // Ignore disconnect errors
            }
        }
    }
}
