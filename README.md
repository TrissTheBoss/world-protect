# World Protect

[![GitHub License](https://img.shields.io/github/license/TrissTheBoss/world-protect)](https://github.com/TrissTheBoss/world-protect/blob/main/LICENSE)
[![Build Status](https://github.com/TrissTheBoss/world-protect/actions/workflows/build.yml/badge.svg)](https://github.com/TrissTheBoss/world-protect/actions/workflows/build.yml)
[![Paper Version](https://img.shields.io/badge/Paper-1.21.11-blue)](https://papermc.io)
[![Java Version](https://img.shields.io/badge/Java-17+-orange)](https://adoptium.net)

**World Protect** is a modern, high-performance world protection plugin for Minecraft Paper/Folia servers. It provides granular region-based protection with a comprehensive flag system, intuitive selection tools, and full Folia compatibility.

## ✨ Features

- **Region-based Protection**: Define custom protected areas with precise boundaries
- **Comprehensive Flag System**: 50+ protection flags for blocks, entities, interactions, and more
- **Folia Support**: Fully compatible with Paper's Folia threading model
- **Visual Selection Tools**: In-game wand selection with particle visualization
- **Priority System**: Handle overlapping regions with configurable priority levels
- **Permission Integration**: Fine-grained permission nodes for all commands and flags
- **Persistent Storage**: SQLite/MySQL support for region data
- **API for Developers**: Clean, documented API for other plugins

## 📦 Quick Start

### Requirements
- Minecraft Server: **Paper 1.17+** or **Folia**
- Java: **17** or higher
- 100MB free disk space for region data

### Installation
1. Download the latest `WorldProtect.jar` from [Releases](https://github.com/TrissTheBoss/world-protect/releases)
2. Place it in your server's `plugins/` directory
3. Restart your server
4. Configure regions using in-game commands or edit `plugins/WorldProtect/config.yml`

### Basic Usage
```bash
# Give yourself the selection wand
/wp wand

# Select two corners of a region
# (Right-click and left-click blocks)

# Create a protected region
/wp create spawn_protection

# Add protection flags
/wp flag spawn_protection block-break deny
/wp flag spawn_protection pvp deny
/wp flag spawn_protection entry allow

# Check region info
/wp info spawn_protection
```

## 🛠️ Commands & Permissions

See [Commands and Permissions](docs/COMMANDS_AND_PERMISSIONS.md) for complete command reference.

### Essential Commands:
- `/wp wand` - Get selection wand
- `/wp create <name>` - Create region from selection
- `/wp delete <name>` - Delete region
- `/wp flag <region> <flag> <value>` - Set protection flag
- `/wp info <region>` - Show region information
- `/wp list` - List all regions

### Essential Permissions:
- `worldprotect.admin` - Full access to all commands
- `worldprotect.wand` - Use selection wand
- `worldprotect.create` - Create regions
- `worldprotect.delete` - Delete own regions

## ⚙️ Configuration

World Protect uses YAML configuration files. See [Configuration Guide](docs/CONFIGURATION.md) for detailed options.

Main configuration files:
- `config.yml` - Plugin settings, database, default flags
- `messages.yml` - Customizable messages and translations
- `flags.yml` - Flag defaults and aliases

## 🏗️ Architecture

World Protect is built with performance and extensibility in mind:

- **Modular Design**: Separated concerns for regions, flags, commands, and storage
- **Thread-Safe**: Proper synchronization for Folia compatibility
- **Event-Driven**: Efficient event handling with priority-based filtering
- **Caching Layer**: Region lookup caching for high-performance checks

See [Architecture Documentation](docs/ARCHITECTURE.md) for technical details.

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for details on:
- Setting up the development environment
- Code style and conventions
- Submitting pull requests
- Reporting issues

## 📚 Documentation

Complete documentation is available in the [`docs/`](docs/) directory:

- [Commands & Permissions](docs/COMMANDS_AND_PERMISSIONS.md)
- [Flag Reference](docs/FLAGS.md)
- [Configuration Guide](docs/CONFIGURATION.md)
- [Regions & Selections](docs/REGIONS_AND_SELECTIONS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Development Notes](docs/DEVELOPMENT_NOTES.md)
- [Git & Release Workflow](docs/GIT_AND_RELEASE_WORKFLOW.md)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [PaperMC](https://papermc.io) for the excellent Paper/Folia APIs
- [WorldGuard](https://dev.bukkit.org/projects/worldguard) for inspiration
- All contributors and testers

---

**Need Help?**
- Check the [documentation](docs/)
- Open an [issue](https://github.com/TrissTheBoss/world-protect/issues)
- Join our Discord (link coming soon)
