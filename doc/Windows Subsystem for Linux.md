Run Warciv.net on Windows Subsystem for Linux (WSL)

=================================================

  

Allows running Freeciv3D on an Ubuntu Linux in Windows.

  
## 1. Running Warciv on WSL:

### 1.1. Install WSL on Windows 11:

https://learn.microsoft.com/en-us/windows/wsl/install

Open a Powershell window, run as Administator, this command:

> wsl --install

  

### 1.2. Git clone Warciv:

> git clone https://github.com/warciv/warciv.git --depth=10

  

### 1.3. Build Warciv:

> cd warciv

> ./scripts/install/install.sh --mode=TEST

  

### 1.4. Start Warciv:

> ./scripts/start-freeciv-web.sh

  

### OpenWarciv at http://localhost/

<br />

