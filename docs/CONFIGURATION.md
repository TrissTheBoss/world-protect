# Configuration Guide

Complete reference for all configuration options in World Protect.

## 📁 Configuration Files

World Protect uses multiple configuration files for different purposes:

1. **`config.yml`** - Main plugin settings and defaults
2. **`messages.yml`** - Customizable messages and translations
3. **`flags.yml`** - Flag defaults and aliases (optional)
4. **`database.yml`** - Database connection settings (optional)

## ⚙️ Main Configuration (`config.yml`)

### Plugin Settings
```yaml
# config.yml
plugin:
  # Plugin information
  name: "WorldProtect"
  version: "1.0.0"
  
  # Debug and logging
  debug: false
  log-level: "INFO" # DEBUG, INFO, WARN, ERROR
  
  # Update checking
  update-checker: true
  auto-download-updates: false
  
  # Metrics (bStats)
  metrics: true
```

### Region Settings
```yaml
regions:
  # Region limits
  max-regions-per-player: 10
  max-region-volume: 1000000 # blocks
  min-region-volume: 1
  
  # Region naming
  name-regex: "^[a-zA-Z0-9_-]{1,64}$"
  reserved-names:
    - "global"
    - "default"
    - "world"
  
  # Selection tool
  selection-wand: "golden_axe"
  selection-particle: "VILLAGER_HAPPY"
  selection-color: "GREEN"
  
  # Visual feedback
  show-boundaries: true
  boundary-particle: "REDSTONE"
  boundary-interval: 20 # ticks
```

### Flag System
```yaml
flags:
  # Default flag values
  defaults:
    block-break: "deny"
    block-place: "deny"
    pvp: "deny"
    entry: "allow"
    mob-spawning: "deny"
    explosions: "deny"
    fire-spread: "deny"
  
  # Flag aliases (shortcuts)
  aliases:
    build: ["block-break", "block-place"]
    protect: ["block-break", "block-place", "pvp"]
    safe: ["block-break", "block-place", "pvp", "mob-spawning"]
  
  # Flag inheritance
  inheritance-enabled: true
  inheritance-depth: 5 # Maximum inheritance levels
```

### Storage Settings
```yaml
storage:
  # Storage backend (sqlite, mysql, h2, yaml)
  type: "sqlite"
  
  # SQLite settings
  sqlite:
    file: "plugins/WorldProtect/regions.db"
    pool-size: 10
    
  # MySQL settings
  mysql:
    host: "localhost"
    port: 3306
    database: "worldprotect"
    username: "minecraft"
    password: "password"
    pool-size: 20
    connection-timeout: 30000
    idle-timeout: 600000
    
  # Caching
  cache:
    enabled: true
    size: 1000
    expire-after-write: 300 # seconds
    expire-after-access: 60 # seconds
```

### Performance Settings
```yaml
performance:
  # Event processing
  async-event-processing: true
  event-priority: "NORMAL" # LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR
  
  # Caching
  region-cache-size: 1000
  flag-cache-size: 5000
  
  # Spatial indexing
  spatial-index-type: "grid" # grid, rtree, quadtree
  grid-cell-size: 16 # chunks
  
  # Thread pool
  thread-pool-size: 4
  max-queue-size: 1000
```

### World Settings
```yaml
worlds:
  # Per-world defaults
  defaults:
    enabled: true
    priority: 0
    
  # World-specific overrides
  overrides:
    world_nether:
      flags:
        pvp: "allow"
        fire-spread: "allow"
    world_the_end:
      flags:
        dragon-fight: "allow"
```

## 💬 Messages Configuration (`messages.yml`)

### Message Format
```yaml
messages:
  # Message format
  prefix: "&8[&3WorldProtect&8]&r"
  error-prefix: "&c✗&r"
  success-prefix: "&a✓&r"
  info-prefix: "&7ℹ&r"
  
  # Color codes
  primary: "&3"
  secondary: "&7"
  accent: "&6"
  error: "&c"
  success: "&a"
```

### Command Messages
```yaml
commands:
  # General command responses
  no-permission: "{prefix} &cYou don't have permission to do that."
  player-only: "{prefix} &cThis command can only be used by players."
  console-only: "{prefix} &cThis command can only be used from console."
  
  # Region commands
  region-created: "{prefix} &aRegion &7{region}&a created successfully."
  region-deleted: "{prefix} &aRegion &7{region}&a deleted successfully."
  region-not-found: "{prefix} &cRegion &7{region}&c not found."
  region-exists: "{prefix} &cRegion &7{region}&c already exists."
  
  # Flag commands
  flag-set: "{prefix} &aFlag &7{flag}&a set to &7{value}&a for region &7{region}&a."
  flag-invalid: "{prefix} &cInvalid flag value. Valid values: {values}"
  
  # Selection commands
  selection-complete: "{prefix} &aSelection complete. Volume: &7{volume}&a blocks."
  selection-incomplete: "{prefix} &cPlease select both positions first."
  wand-received: "{prefix} &aYou received the selection wand."
```

### Error Messages
```yaml
errors:
  # General errors
  internal-error: "{prefix} &cAn internal error occurred. Please check console."
  configuration-error: "{prefix} &cConfiguration error: {error}"
  database-error: "{prefix} &cDatabase error: {error}"
  
  # Validation errors
  invalid-region-name: "{prefix} &cInvalid region name. Must match: {regex}"
  region-too-large: "{prefix} &cRegion is too large. Maximum: {max} blocks."
  region-too-small: "{prefix} &cRegion is too small. Minimum: {min} blocks."
  cross-world-selection: "{prefix} &cCannot create regions across different worlds."
  
  # Permission errors
  cannot-modify-region: "{prefix} &cYou cannot modify region &7{region}&c."
  cannot-delete-region: "{prefix} &cYou cannot delete region &7{region}&c."
```

### Help Messages
```yaml
help:
  header: "&3=== &bWorld Protect Help &3==="
  footer: "&3=== &bPage {page}/{total} &3==="
  command-format: "&7/{command} &f{args} &8- &7{description}"
  
  pages:
    1:
      - "&bEssential Commands:"
      - "  &7/wp wand &8- &7Get selection wand"
      - "  &7/wp create <name> &8- &7Create region"
      - "  &7/wp delete <name> &8- &7Delete region"
      - "  &7/wp flag <region> <flag> <value> &8- &7Set flag"
    2:
      - "&bRegion Management:"
      - "  &7/wp list [world] &8- &7List regions"
      - "  &7/wp info <region> &8- &7Region info"
      - "  &7/wp priority <region> <priority> &8- &7Set priority"
      - "  &7/wp redefine <name> &8- &7Redefine region"
```

## 🏷️ Flag Configuration (`flags.yml`)

### Custom Flag Definitions
```yaml
# flags.yml
flags:
  # Custom flags (advanced users)
  custom:
    my-custom-flag:
      name: "my-custom-flag"
      description: "My custom protection flag"
      default: "allow"
      category: "CUSTOM"
      values: ["allow", "deny", "members"]
      events:
        - "org.bukkit.event.player.PlayerInteractEvent"
      
    another-flag:
      name: "another-flag"
      description: "Another custom flag"
      default: "deny"
      category: "CUSTOM"
      values: ["allow", "deny"]
  
  # Flag groups
  groups:
    building:
      name: "Building Protection"
      flags: ["block-break", "block-place", "block-interact"]
      description: "All building-related flags"
    
    combat:
      name: "Combat Protection"
      flags: ["pvp", "damage-players", "damage-animals", "damage-monsters"]
      description: "All combat-related flags"
```

## 🗄️ Database Configuration (`database.yml`)

### Connection Pool
```yaml
# database.yml
connection-pool:
  # Connection settings
  maximum-pool-size: 10
  minimum-idle: 2
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000
  
  # Connection testing
  connection-test-query: "SELECT 1"
  validation-timeout: 5000
  
  # Leak detection
  leak-detection-threshold: 60000
```

### Migration Settings
```yaml
migrations:
  # Migration settings
  enabled: true
  locations: ["classpath:db/migration"]
  baseline-on-migrate: true
  baseline-version: "1"
  
  # Migration scripts location
  sql-migration-prefix: "V"
  sql-migration-separator: "__"
  sql-migration-suffixes: [".sql"]
```

## 🔧 Advanced Configuration

### Event Listeners
```yaml
events:
  # Enable/disable specific event listeners
  listeners:
    block-break: true
    block-place: true
    player-interact: true
    entity-damage: true
    player-move: true
    inventory-open: true
    
  # Event priority overrides
  priorities:
    block-break: "HIGH"
    pvp: "HIGHEST"
    entry: "NORMAL"
    
  # Event filtering
  filters:
    ignore-creative: false
    ignore-op: false
    ignore-bypass-permission: false
```

### API Settings
```yaml
api:
  # Public API settings
  enabled: true
  version: "1.0"
  
  # Hook settings
  hooks:
    worldedit:
      enabled: true
      selection-integration: true
      
    vault:
      enabled: true
      economy-integration: true
      permission-integration: true
      
    placeholderapi:
      enabled: true
      placeholders: true
```

### Backup Settings
```yaml
backup:
  # Automatic backups
  enabled: true
  interval: 86400 # seconds (24 hours)
  keep-backups: 7
  compression: true
  
  # Backup locations
  locations:
    - "plugins/WorldProtect/regions.db"
    - "plugins/WorldProtect/config.yml"
    
  # Backup format
  format: "zip" # zip, tar, tar.gz
```

## 🎨 Theme Configuration

### GUI Themes
```yaml
gui:
  # GUI settings
  enabled: true
  theme: "dark" # dark, light, custom
  
  # Dark theme
  dark:
    background: "&8"
    primary: "&3"
    secondary: "&7"
    accent: "&6"
    text: "&f"
    
  # Light theme  
  light:
    background: "&f"
    primary: "&9"
    secondary: "&8"
    accent: "&e"
    text: "&0"
    
  # Custom theme
  custom:
    colors:
      background: "#2D3748"
      primary: "#4299E1"
      secondary: "#A0AEC0"
      accent: "#ED8936"
      text: "#FFFFFF"
```

## 🔄 Configuration Reload

### Reload Command
```bash
# Reload all configuration files
/wp reload

# Reload specific configuration
/wp reload config
/wp reload messages
/wp reload flags
```

### Hot Reload
```yaml
config:
  # Watch for configuration changes
  watch-for-changes: true
  check-interval: 5 # seconds
  
  # Auto-reload on change
  auto-reload:
    config.yml: true
    messages.yml: true
    flags.yml: false # Manual reload recommended
```

## ⚠️ Configuration Validation

### Validation Rules
```yaml
validation:
  # Type validation
  require-valid-types: true
  
  # Range validation
  ranges:
    max-region-volume:
      min: 1
      max: 10000000
      
    priority:
      min: 0
      max: 1000
      
  # Regex validation
  patterns:
    region-name: "^[a-zA-Z0-9_-]{1,64}$"
    color-code: "^&[0-9a-fk-or]$"
    
  # Dependency validation
  dependencies:
    mysql:
      requires: ["host", "database", "username"]
```

### Configuration Migration
When updating between versions, configuration files are automatically migrated:
```yaml
migration:
  # Auto-migration settings
  enabled: true
  backup-before-migration: true
  migration-scripts: "config/migrations"
  
  # Version tracking
  current-version: "1.0.0"
  target-version: "1.1.0"
```

## 📝 Configuration Examples

### Complete Example
```yaml
# Complete config.yml example
plugin:
  debug: false
  log-level: "INFO"
  update-checker: true

regions:
  max-regions-per-player: 10
  selection-wand: "golden_axe"
  show-boundaries: true

flags:
  defaults:
    block-break: "deny"
    pvp: "deny"
    entry: "allow"

storage:
  type: "sqlite"
  sqlite:
    file: "plugins/WorldProtect/regions.db"

messages:
  prefix: "&8[&3WP&8]&r"
  region-created: "{prefix} &aRegion created!"
```

### Production Configuration
```yaml
# Production server configuration
plugin:
  debug: false
  log-level: "WARN"
  metrics: true

regions:
  max-region-volume: 500000
  show-boundaries: false # Better performance

storage:
  type: "mysql"
  mysql:
    host: "db.example.com"
    database: "mc_worldprotect"
    username: "mc_user"
    password: "${DB_PASSWORD}" # Environment variable

performance:
  async-event-processing: true
  region-cache-size: 5000
```

### Development Configuration
```yaml
# Development configuration
plugin:
  debug: true
  log-level: "DEBUG"

regions:
  max-regions-per-player: 100
  show-boundaries: true

storage:
  type: "sqlite"
  sqlite:
    file: ":memory:" # In-memory database for testing

performance:
  async-event-processing: false # Easier debugging
```

---

*Configuration files support comments (lines starting with #) and environment variable substitution using `${VARIABLE_NAME}` syntax.*