package com.example.myapplication.ssh

data class SshConfig(
    val host: String = "",
    val port: Int = 22,
    val username: String = "",
    val password: String = "",
    val sudoPassword: String = "",
    val useSshKey: Boolean = false,
    val privateKeyPath: String = ""
)
