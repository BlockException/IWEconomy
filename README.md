# IWEconomy

A professional, high-performance Economy Plugin for Minecraft 1.21+ (Paper).
IWEconomy provides a modern Vault integration and user-friendly GUI interfaces using Anvil menus for economy transactions.

## Features

* **Vault Integration**: Fully compatible with the standard Vault API.
* **Modern Formatting**: Uses MiniMessage API for complete Hex color support and gradient formatting.
* **Anvil GUI**: Modern UI approach using Anvil menus for `/pay` and `/eco` inputs to prevent chat-based command errors.
* **Multi-Language Support**: Built-in support for 30 languages, dynamically configurable.
* **High Performance**: Optimized for Java 21 and modern Paper environments.

## Commands & Permissions

| Command | Description | Permission |
|---------|-------------|------------|
| `/pay <player>` | Transfer money to another player via Anvil GUI | `iweconomy.pay` (Default: true) |
| `/balance`, `/bal` | Check your current account balance | `iweconomy.balance` (Default: true) |
| `/eco <action> <player>` | Manage economy (add, set, remove, reset) | `iweconomy.manage.admin` (Default: op) |
| `/ecoreload` | Reload the plugin configuration and language files | `iweconomy.manage.reload.admin` (Default: op) |

## Requirements

* **Minecraft Server**: Paper 1.21 or higher
* **Java**: Version 21 or higher
* **Dependencies**: 
  * [Vault](https://dev.bukkit.org/projects/vault) (Hard Dependency)
  * [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) (Hard Dependency)
  * Any Permissions Plugin (e.g. LuckPerms)

## Installation

1. Download the latest `IWEconomy.jar` from the releases section.
2. Place the jar file into your server's `plugins` folder.
3. Ensure Vault is installed.
4. Restart your server.
5. Configure the plugin in `plugins/IWEconomy/config.yml`.

## Configuration

The default configuration file allows you to set the currency symbol, default starting balance, and the server language.

```yaml
currency-symbol: "$"
starting-balance: 0.0
language: "en"
```

## Compilation

To compile this project yourself, you need Maven installed.

```bash
git clone https://github.com/BlockException_/IWEconomy.git
cd IWEconomy
mvn clean package
```

The compiled jar file will be available in the `target/` directory.

## License

This project is proprietary and belongs to [iwmedia](https://iwmedia.eu). Unauthorized distribution or modification is prohibited unless explicitly stated otherwise.
