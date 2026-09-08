# NixOS Control ❄️📱

**NixOS Control** is a specialized Android remote-control dashboard built with **Kotlin** and **Jetpack Compose**. It allows you to manage your NixOS (or any Linux) machine over the local network with a single tap, using secure SSH connectivity.

Unlike generic terminal apps, NixOS Control provides a dedicated, high-productivity grid of buttons that trigger specific shell commands you define.

---

## 🚀 Key Features

### 🛠️ Fully Dynamic Dashboard
- **Add, Edit, & Delete:** Create buttons for any command directly within the app.
- **2-Column Grid Layout:** Optimized UI for quick access to multiple controls.
- **Persistent Storage:** All custom actions are saved in a local **Room Database**.

### 🔒 Security & Privacy
- **Encrypted Storage:** SSH and Sudo credentials are encrypted at rest using **Android Keystore** via `EncryptedSharedPreferences`.
- **Zero Cloud Dependency:** Works 100% offline on your local Wi-Fi. No accounts, no data collection.
- **Sudo Integration:** Handles privileged commands automatically by securely piping passwords to `sudo -S`.

### ⚙️ Advanced Connectivity
- **mDNS Support:** Connect using hostnames like `nixos.local` instead of tracking dynamic IPs.
- **Modern Cryptography:** Includes **BouncyCastle** support for high-security SSH key exchanges (like X25519).
- **Custom Icons:** Assign meaningful icons (Power, Wifi, Terminal, etc.) to your custom buttons.

---

## 🛠️ Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room Persistence Library
- **Networking:** SSHJ + BouncyCastle
- **Storage:** Jetpack Security (Crypto)
- **Asynchrony:** Kotlin Coroutines & StateFlow

---

## 📦 Getting Started

### 1. NixOS Configuration
Enable SSH and ensure your user has appropriate permissions in your `configuration.nix`:

```nix
{ config, pkgs, ... }: {
  # Enable OpenSSH
  services.openssh.enable = true;

  # Open Firewall
  networking.firewall.allowedTCPPorts = [ 22 ];

  # Configure User
  users.users.your_user = {
    isNormalUser = true;
    extraGroups = [ "wheel" "networkmanager" ];
  };
}
```

### 2. App Configuration
1. Install the **Release APK**.
2. Tap the **Settings (⚙️)** icon.
3. Enter your PC's Host (e.g., `192.168.1.5` or `nixos.local`), Username, and Password.
4. Save and use the **Test Connection** button to verify.

---

## 📂 Project Structure

```text
app/src/main/java/com/example/myapplication/
├── model/        # Room Entities, DAOs, and Data Classes
├── repository/   # Data handling and SSH orchestration
├── ssh/          # SSH Client implementation and Security Helpers
├── ui/           # Compose Screens and Themes
└── viewmodel/    # UI logic and state management
```

## 📜 License
This project is open-source and intended for personal use.

---
*Developed for the NixOS community with ❤️.*
