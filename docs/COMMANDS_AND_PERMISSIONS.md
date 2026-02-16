# Commands and Permissions

Complete reference for all World Protect commands and their associated permissions.

## 📋 Command Overview

World Protect uses `/wp` as the main command prefix. All subcommands follow this pattern.

### Command Structure
```
/wp <subcommand> [arguments] [flags]
```

### Permission Structure
```
worldprotect.<command>        # Basic command access
worldprotect.<command>.<arg>  # Specific argument access
worldprotect.admin            # Full administrative access
```

## 🎯 Essential Commands

### `/wp help [page]`
**Description**: Shows help information and command list.

**Arguments**:
- `page` (optional): Page number of help (default: 1)

**Permission**: `worldprotect.help` (default: true)

**Examples**:
```
/wp help
/wp help 2
```

### `/wp wand`
**Description**: Gives the selection wand tool.

**Permission**: `worldprotect.wand`

**Examples**:
```
/wp wand
```

### `/wp create <name>`
**Description**: Creates a new region from your current selection.

**Arguments**:
- `name`: Unique name for the region (alphanumeric, underscores)

**Permission**: `worldprotect.create`

**Examples**:
```
/wp create spawn_protection
/wp create shop_area
```

### `/wp delete <name>`
**Description**: Deletes an existing region.

**Arguments**:
- `name`: Name of region to delete

**Permission**: `worldprotect.delete` (own regions) or `worldprotect.delete.others` (any region)

**Examples**:
```
/wp delete old_region
```

### `/wp flag <region> <flag> <value>`
**Description**: Sets a protection flag on a region.

**Arguments**:
- `region`: Target region name
- `flag`: Flag name (see FLAGS.md)
- `value`: Flag value (allow, deny, etc.)

**Permission**: `worldprotect.flag`

**Examples**:
```
/wp flag spawn block-break deny
/wp flag shop entry allow
/wp flag arena pvp allow
```

## 📊 Region Management Commands

### `/wp list [world]`
**Description**: Lists all regions, optionally filtered by world.

**Arguments**:
- `world` (optional): World name filter

**Permission**: `worldprotect.list`

**Examples**:
```
/wp list
/wp list world_nether
```

### `/wp info <region>`
**Description**: Shows detailed information about a region.

**Arguments**:
- `region`: Region name to inspect

**Permission**: `worldprotect.info`

**Examples**:
```
/wp info spawn
```

### `/wp redefine <name>`
**Description**: Redefines a region's boundaries from current selection.

**Arguments**:
- `name`: Region to redefine

**Permission**: `worldprotect.redefine`

**Examples**:
```
/wp redefine expanded_spawn
```

### `/wp priority <region> <priority>`
**Description**: Sets region priority (higher = more important).

**Arguments**:
- `region`: Target region
- `priority`: Priority number (0-1000)

**Permission**: `worldprotect.priority`

**Examples**:
```
/wp priority spawn 100
```

## 👥 Member Management Commands

### `/wp member add <region> <player> [permission]`
**Description**: Adds a player as member to a region.

**Arguments**:
- `region`: Target region
- `player`: Player name or UUID
- `permission` (optional): Permission level (member, owner) (default: member)

**Permission**: `worldprotect.member.add`

**Examples**:
```
/wp member add spawn Notch
/wp member add shop Alex owner
```

### `/wp member remove <region> <player>`
**Description**: Removes a player from region members.

**Arguments**:
- `region`: Target region
- `player`: Player name or UUID

**Permission**: `worldprotect.member.remove`

**Examples**:
```
/wp member remove spawn Notch
```

### `/wp member list <region>`
**Description**: Lists all members of a region.

**Arguments**:
- `region`: Target region

**Permission**: `worldprotect.member.list`

**Examples**:
```
/wp member list spawn
```

## 🎨 Selection Commands

### `/wp pos1`
**Description**: Sets position 1 to your current location.

**Permission**: `worldprotect.select`

**Examples**:
```
/wp pos1
```

### `/wp pos2`
**Description**: Sets position 2 to your current location.

**Permission**: `worldprotect.select`

**Examples**:
```
/wp pos2
```

### `/wp expand <direction> <amount>`
**Description**: Expands selection in specified direction.

**Arguments**:
- `direction`: up, down, north, south, east, west, vertical, horizontal, all
- `amount`: Blocks to expand

**Permission**: `worldprotect.select`

**Examples**:
```
/wp expand up 10
/wp expand all 5
```

### `/wp contract <direction> <amount>`
**Description**: Contracts selection in specified direction.

**Arguments**:
- `direction`: up, down, north, south, east, west, vertical, horizontal, all
- `amount`: Blocks to contract

**Permission**: `worldprotect.select`

**Examples**:
```
/wp contract down 5
```

## 🔧 Utility Commands

### `/wp reload`
**Description**: Reloads configuration files.

**Permission**: `worldprotect.reload`

**Examples**:
```
/wp reload
```

### `/wp version`
**Description**: Shows plugin version information.

**Permission**: `worldprotect.version` (default: true)

**Examples**:
```
/wp version
```

### `/wp debug [toggle]`
**Description**: Toggles debug mode for troubleshooting.

**Arguments**:
- `toggle` (optional): on, off, or toggle

**Permission**: `worldprotect.debug`

**Examples**:
```
/wp debug on
/wp debug toggle
```

### `/wp import <file>`
**Description**: Imports regions from file.

**Arguments**:
- `file`: File to import (JSON or YAML)

**Permission**: `worldprotect.import`

**Examples**:
```
/wp import backup.json
```

### `/wp export <file>`
**Description**: Exports regions to file.

**Arguments**:
- `file`: Output file (JSON or YAML)

**Permission**: `worldprotect.export`

**Examples**:
```
/wp export backup.json
```

## 🔐 Permission Reference

### Administrative Permissions
| Permission | Description | Default |
|------------|-------------|---------|
| `worldprotect.admin` | Full access to all commands | op |
| `worldprotect.*` | Wildcard for all permissions | op |

### Command Categories
| Category | Permission Prefix | Description |
|----------|------------------|-------------|
| Basic | `worldprotect.` | Core command access |
| Region | `worldprotect.region.` | Region management |
| Member | `worldprotect.member.` | Member management |
| Selection | `worldprotect.select.` | Selection tools |
| Utility | `worldprotect.util.` | Utility commands |

### Default Permission Assignments
```yaml
# Default permissions in plugin.yml
permissions:
  worldprotect.admin:
    description: Full administrative access
    default: op
    children:
      worldprotect.*: true
  
  worldprotect.user:
    description: Basic user permissions
    default: true
    children:
      worldprotect.help: true
      worldprotect.version: true
      worldprotect.wand: true
      worldprotect.info: true
      worldprotect.list: true
  
  worldprotect.builder:
    description: Region builder permissions
    default: op
    children:
      worldprotect.create: true
      worldprotect.delete: true
      worldprotect.flag: true
      worldprotect.select: true
      worldprotect.redefine: true
```

### Permission Inheritance
```
worldprotect.admin
├── worldprotect.*
│   ├── worldprotect.help
│   ├── worldprotect.wand
│   ├── worldprotect.create
│   └── ...
└── worldprotect.user
    └── worldprotect.builder
```

## 🎮 Command Examples

### Complete Workflow
```bash
# 1. Get selection tool
/wp wand

# 2. Select area (right-click and left-click blocks)

# 3. Create region
/wp create town_square

# 4. Set protection flags
/wp flag town_square block-break deny
/wp flag town_square block-place deny
/wp flag town_square pvp deny
/wp flag town_square entry allow
/wp flag town_square mob-spawning deny

# 5. Add trusted members
/wp member add town_square MayorSteve owner
/wp member add town_square DeputyAlex member

# 6. Check region info
/wp info town_square
```

### Advanced Examples
```bash
# Set priority for overlapping regions
/wp priority spawn 100
/wp priority shop 50

# Expand selection upward
/wp expand up 10

# Import regions from backup
/wp import regions_backup.json

# Toggle debug mode
/wp debug on
```

## ⚠️ Command Limitations

### Region Name Restrictions
- Maximum length: 64 characters
- Allowed characters: a-z, A-Z, 0-9, _, -
- Case-insensitive (spawn = SPAWN = Spawn)

### Selection Limits
- Maximum volume: 1,000,000 blocks (configurable)
- Minimum volume: 1 block
- Cross-world selections: Not allowed

### Permission Overrides
- OPs bypass all permission checks
- `worldprotect.admin` grants all permissions
- Specific permissions override category permissions

## 🔄 Auto-Generated Documentation

*Note: This document is partially auto-generated from the command registry. When new commands are added to the codebase, run the documentation sync process to update this file.*

**Last Generated**: 2024-01-01  
**Command Count**: 24  
**Permission Nodes**: 42