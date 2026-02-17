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
worldprotect.command.<command>        # Basic command access
worldprotect.admin                    # Full administrative access
```

## 🎯 Essential Commands

### `/wp help`
**Description**: Shows help information and command list.

**Permission**: `worldprotect.command.help` (default: true)

**Examples**:
```
/wp help
```

### `/wp create <name>`
**Description**: Creates a new area from your current selection.

**Arguments**:
- `name`: Unique name for the area

**Permission**: `worldprotect.command.create`

**Examples**:
```
/wp create spawn_protection
/wp create shop_area
```

### `/wp delete <name>`
**Description**: Deletes an existing area.

**Arguments**:
- `name`: Name of area to delete

**Permission**: `worldprotect.command.delete`

**Examples**:
```
/wp delete old_area
```

### `/wp list`
**Description**: Lists all areas.

**Permission**: `worldprotect.command.list`

**Examples**:
```
/wp list
```

### `/wp info <name>`
**Description**: Shows detailed information about an area.

**Arguments**:
- `name`: Area name to inspect

**Permission**: `worldprotect.command.info`

**Examples**:
```
/wp info spawn
```

### `/wp flags`
**Description**: Lists all available flags. Can also be used to set flags: `/wp flags <area> <flag> <value>`.

**Permission**: `worldprotect.command.flags`

**Examples**:
```
/wp flags
/wp flags spawn pvp deny
/wp flags arena explosions allow
```

### `/wp flag <area> <flag> <value>`
**Description**: Sets a flag value for an area. (Alias for `/wp flags <area> <flag> <value>`).

**Arguments**:
- `area`: Name of the area
- `flag`: Flag name to set
- `value`: Value (true/false, allow/deny, 1/0)

**Permission**: `worldprotect.command.flag`

**Examples**:
```
/wp flag spawn pvp deny
/wp flag arena explosions allow
```

### `/wp wand`
**Description**: Gives you the selection wand.

**Permission**: `worldprotect.command.wand`

**Examples**:
```
/wp wand
```

### `/wp reload`
**Description**: Reloads plugin configuration.

**Permission**: `worldprotect.command.reload`

**Examples**:
```
/wp reload
```

### `/wp version`
**Description**: Shows plugin version information.

**Permission**: `worldprotect.command.version` (default: true)

**Examples**:
```
/wp version
```

### `/wp cancel`
**Description**: Cancels your current selection.

**Permission**: `worldprotect.command.cancel`

**Examples**:
```
/wp cancel
```

### `/wp here`
**Description**: Shows areas at your current location.

**Permission**: `worldprotect.command.here`

**Examples**:
```
/wp here
/wp here flags
/wp here visualize
```

### `/wp circle <name> <radius> [height]`
**Description**: Creates a circular area centered at your location.

**Arguments**:
- `name`: Unique name for the area
- `radius`: Radius of the circle in blocks
- `height`: Optional height of the cylinder (default: radius * 2)

**Permission**: `worldprotect.command.circle`

**Examples**:
```
/wp circle spawn 10
/wp circle arena 20 10
```

### `/wp selection new <name>`
**Description**: Starts a new selection with the given name.

**Arguments**:
- `name`: Name for the selection

**Permission**: `worldprotect.command.selection`

**Examples**:
```
/wp selection new myarea
```

### `/wp selection info`
**Description**: Shows information about your current selection.

**Permission**: `worldprotect.command.selection`

**Examples**:
```
/wp selection info
```

## 🔐 Permission Reference

### Administrative Permissions
| Permission | Description | Default |
|------------|-------------|---------|
| `worldprotect.admin` | Full access to all commands | op |
| `worldprotect.command.*` | All command permissions | op |

### Command Permissions
| Permission | Description | Default |
|------------|-------------|---------|
| `worldprotect.command.help` | Access to help command | true |
| `worldprotect.command.create` | Create new areas | op |
| `worldprotect.command.delete` | Delete areas | op |
| `worldprotect.command.list` | List all areas | true |
| `worldprotect.command.info` | View area information | true |
| `worldprotect.command.flags` | List available flags | true |
| `worldprotect.command.flag` | Set area flags | op |
| `worldprotect.command.wand` | Get selection wand | op |
| `worldprotect.command.reload` | Reload plugin configuration | op |
| `worldprotect.command.version` | View plugin version | true |
| `worldprotect.command.cancel` | Cancel current selection | op |
| `worldprotect.command.here` | Show areas at location | true |
| `worldprotect.command.circle` | Create circular area | op |
| `worldprotect.command.selection` | Manage selections | op |

### Permission Inheritance
```
worldprotect.admin
└── worldprotect.command.*
    ├── worldprotect.command.help
    ├── worldprotect.command.create
    ├── worldprotect.command.delete
    ├── worldprotect.command.list
    ├── worldprotect.command.info
    ├── worldprotect.command.flags
    ├── worldprotect.command.flag
    ├── worldprotect.command.wand
    ├── worldprotect.command.reload
    ├── worldprotect.command.version
    ├── worldprotect.command.cancel
    ├── worldprotect.command.here
    ├── worldprotect.command.circle
    └── worldprotect.command.selection
```

## 🎮 Command Examples

### Complete Workflow
```bash
# 1. Use selection wand (right-click and left-click blocks)

# 2. Create area
/wp create town_square

# 3. Check area information
/wp info town_square

# 4. List all areas
/wp list

# 5. View available flags
/wp flags

# 6. Reload configuration (admin only)
/wp reload
```

## ⚠️ Command Limitations

### Area Name Restrictions
- Maximum length: 64 characters
- Allowed characters: a-z, A-Z, 0-9, _, -
- Case-insensitive

### Selection Requirements
- Need at least 2 points for point-based selection
- Need at least 3 points for free-draw selection
- Cross-world selections: Not allowed

### Permission Overrides
- OPs bypass all permission checks
- `worldprotect.admin` grants all permissions

## 🔄 Auto-Generated Documentation

*Note: This document is auto-generated from the command registry. When new commands are added to the codebase, the documentation will be updated automatically.*

**Last Generated**: 2026-02-17  
**Command Count**: 15  
**Permission Nodes**: 17
