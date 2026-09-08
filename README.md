# Linux Remote Control 🐧📱

[![Download APK](https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android)](https://github.com/Hmaidouch/NixOS-Control/raw/main/releases/NixOS-Control-v1.0.apk)

**Linux Remote Control** is a clean, modern Android dashboard designed to control **any Linux distribution** (NixOS, Ubuntu, Arch, Fedora, Debian, etc.) over a local network via SSH. 

While it was originally developed for NixOS, it works perfectly with any Linux machine—all you need is a running SSH server.

---
## Screenshots :

<div align="center">
  <img width="220" alt="Dashboard" src="https://github.com/user-attachments/assets/c35dfb63-023c-4716-a046-585a08f067e1" />
  <img width="220" alt="Settings" src="https://github.com/user-attachments/assets/a96316c1-d1fc-4769-b9ad-bd0273f63a8a" />
  <img width="220" alt="Control" src="https://github.com/user-attachments/assets/a76b92db-cc06-4525-b5a4-13f7ff6c207b" />
</div>

## 🚀 Key Features

### 🛠️ Universal Linux Compatibility
- **Works with any Distro:** Whether it's a powerful NixOS workstation, an Ubuntu laptop, or a Raspberry Pi server.
- **Dynamic Dashboard:** Add, edit, and delete buttons for any command directly from the app interface.
- **2-Column Grid:** Optimized UI for quick access to multiple controls.

### ⚡ Smart Execution
- **Sudo Integration:** Automatically handles privileged commands by securely piping passwords to `sudo -S`.
- **Customizable Actions:** Create buttons for anything: Shutdown, Volume control, Service restarts, or custom shell scripts.
- **Zero Latency:** Commands are executed instantly over your local Wi-Fi.

### 🔒 Security & Privacy
- **Encrypted Credentials:** SSH and Sudo passwords are encrypted at rest using **Android Keystore** via `EncryptedSharedPreferences`.
- **Local Network Only:** Communication happens strictly on your local network. No cloud, no telemetry, no tracking.
- **Modern Cryptography:** Full support for high-security SSH key exchanges (like X25519) via BouncyCastle.

---

## 📦 Getting Started

### 1. Prepare your Linux Machine
The only requirement is a running SSH server.

**For Ubuntu/Debian:**
```bash
sudo apt update && sudo apt install openssh-server
```

**For Arch Linux:**
```bash
sudo pacman -S openssh && sudo systemctl enable --now sshd
```

**For NixOS:**
Add this to your `configuration.nix`:
```nix
services.openssh.enable = true;
networking.firewall.allowedTCPPorts = [ 22 ];
```

### 2. App Setup
1. Download and install the [Release APK](https://github.com/Hmaidouch/NixOS-Control/raw/main/releases/NixOS-Control-v1.0.apk).
2. Tap the **Settings (⚙️)** icon.
3. Enter your PC's IP (or `.local` hostname), Username, and Password.
4. Save and use the **Test Connection** button to verify.

---

## 🛠️ Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Database:** Room Persistence Library
- **Networking:** SSHJ + BouncyCastle
- **Security:** Jetpack Security (Crypto)
- **Architecture:** MVVM (Model-View-ViewModel)

---
*Empowering Linux users to control their world, one tap at a time.*
