# NixOS Control

**NixOS Control** is a simple, fast, and secure Android application built with Kotlin and Jetpack Compose to remotely control your Linux (NixOS) computer over the local network via SSH.

## Features

- **Dynamic Buttons:** Add, delete, and edit control buttons directly from the app.
- **Secure Storage:** Credentials (SSH and Sudo passwords) are stored securely using Android's `EncryptedSharedPreferences`.
- **Customizable Commands:** Execute any predefined shell command (e.g., shutdown, volume control, application-specific network toggles).
- **Modern UI:** Clean Material 3 dashboard with a 2-column grid layout for easy access.
- **Local Network Support:** Works over the local network without requiring cloud services or external APIs.
- **Smart Host Resolution:** Supports `.local` (mDNS) hostnames for easier connectivity.

## Screenshots

*(Add screenshots here)*

## Getting Started

### Prerequisites

- An Android device running Android 7.0 (API level 24) or higher.
- A Linux/NixOS computer with:
    - `sshd` enabled and reachable on the local network.
    - `sudo` access for privileged commands (like shutdown).
    - `NetworkManager` or other specific tools used in your commands.

### Installation

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and install the APK on your device.

### NixOS Configuration Example

To allow the app to control your NixOS machine, add the following to your `configuration.nix`:

```nix
{ config, pkgs, ... }:

{
  # Enable the SSH daemon
  services.openssh.enable = true;

  # Open the firewall for SSH
  networking.firewall.allowedTCPPorts = [ 22 ];

  # Ensure your user has sudo privileges
  users.users.YOUR_USERNAME = {
    isNormalUser = true;
    extraGroups = [ "wheel" ];
  };
}
```

## How It Works

The app establishes an SSH connection using the `SSHJ` library with `BouncyCastle` for modern cryptographic support. Privileged commands are executed using the `echo 'password' | sudo -S command` pattern to avoid interactive prompts.

## Technology Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM
- **Security:** EncryptedSharedPreferences
- **Database:** Room (for dynamic actions)
- **SSH Library:** SSHJ
- **Networking:** Coroutines & StateFlow

## License

This project is open-source. Feel free to use and modify it for your personal needs.
