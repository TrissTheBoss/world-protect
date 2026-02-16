# World Protect Architecture

This document describes the high-level architecture of World Protect, including core components, data flow, threading model, and design decisions.

## 🏗️ System Overview

World Protect follows a modular, event-driven architecture designed for performance and extensibility. The system is built around these core principles:

1. **Separation of Concerns**: Clear boundaries between regions, flags, commands, and storage
2. **Thread Safety**: Full Folia compatibility with proper synchronization
3. **Extensibility**: Clean API for third-party plugins
4. **Performance**: Caching and efficient algorithms for region lookup

## 📦 Core Components

### 1. Plugin Bootstrap (`WorldProtectPlugin`)
- **Purpose**: Main entry point, manages lifecycle and component initialization
- **Responsibilities**:
  - Load configuration and dependencies
  - Register event listeners and commands
  - Initialize storage and caching layers
  - Handle plugin enable/disable lifecycle

### 2. Region Management (`RegionManager`)
- **Purpose**: Central registry for all protected regions
- **Responsibilities**:
  - Create, update, delete regions
  - Spatial indexing for fast region lookup
  - Handle region overlaps and priority resolution
  - Persist region data to storage

### 3. Flag System (`FlagManager`)
- **Purpose**: Manage protection flags and their values
- **Responsibilities**:
  - Define available flags and their metadata
  - Validate flag values and types
  - Apply flag inheritance and defaults
  - Handle flag conflicts and precedence

### 4. Command System (`CommandRegistry`)
- **Purpose**: Register and handle all plugin commands
- **Responsibilities**:
  - Parse command arguments and permissions
  - Route commands to appropriate handlers
  - Provide tab completion and help
  - Validate command execution context

### 5. Storage Layer (`StorageManager`)
- **Purpose**: Abstract data persistence
- **Responsibilities**:
  - Support multiple backends (SQLite, MySQL)
  - Handle migrations and schema updates
  - Provide caching layer for performance
  - Ensure data consistency and transactions

### 6. Selection System (`SelectionManager`)
- **Purpose**: Handle visual region selection
- **Responsibilities**:
  - Manage player selection sessions
  - Provide wand tool functionality
  - Visual feedback with particles
  - Validate selection boundaries

## 🔄 Data Flow

### Event Processing Pipeline
```
Bukkit Event → Event Listener → Region Lookup → Flag Check → Action Decision
```

1. **Event Capture**: Bukkit events are captured by registered listeners
2. **Region Lookup**: Fast spatial query to find relevant regions
3. **Flag Evaluation**: Check applicable flags in priority order
4. **Decision Making**: Allow/deny based on flag values and permissions
5. **Action Execution**: Execute or cancel the original action

### Region Lookup Optimization
- **Spatial Index**: R-tree or grid-based indexing for O(log n) lookups
- **Caching Layer**: LRU cache for frequently accessed regions
- **World Partitioning**: Separate indexes per world for isolation

## 🧵 Threading Model (Paper vs Folia)

### Paper (Single-Threaded)
- All operations run on main server thread
- Simple synchronization (no thread safety concerns)
- Direct Bukkit API access

### Folia (Region-Scheduled)
- **Region-Specific Threads**: Each world region has dedicated thread
- **Thread Safety**: All shared data must be properly synchronized
- **Async Operations**: Storage and network operations run async

### Implementation Strategy
```java
public class ThreadSafeRegionManager {
    private final ConcurrentHashMap<String, Region> regions;
    private final ReadWriteLock spatialIndexLock;
    
    public boolean isAllowed(Location loc, Flag flag) {
        // Thread-safe region lookup
        List<Region> relevant = findRegions(loc);
        
        // Evaluate flags with proper locking
        return evaluateFlags(relevant, flag);
    }
}
```

## 🗄️ Data Storage

### Schema Design
```sql
-- Regions table
CREATE TABLE regions (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64) UNIQUE,
    world VARCHAR(64),
    min_x INTEGER, min_y INTEGER, min_z INTEGER,
    max_x INTEGER, max_y INTEGER, max_z INTEGER,
    priority INTEGER DEFAULT 0,
    flags TEXT, -- JSON serialized flags
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Region members (players with special access)
CREATE TABLE region_members (
    region_id INTEGER,
    player_uuid VARCHAR(36),
    permission_level INTEGER,
    FOREIGN KEY (region_id) REFERENCES regions(id)
);
```

### Storage Backends
1. **SQLite**: Default, file-based, zero configuration
2. **MySQL**: For multi-server setups, better performance
3. **H2**: Alternative embedded database
4. **YAML**: Legacy support (not recommended for production)

## 🔌 Plugin API

### Public API Surface
```java
public interface WorldProtectAPI {
    // Region management
    Optional<Region> getRegion(String name);
    List<Region> getRegionsAt(Location location);
    Region createRegion(String name, Location min, Location max);
    
    // Flag operations
    boolean isAllowed(Location location, Flag flag);
    void setFlag(Region region, Flag flag, FlagValue value);
    
    // Event hooks
    void registerFlagHandler(FlagHandler handler);
    void registerRegionValidator(RegionValidator validator);
}
```

### Extension Points
1. **Custom Flags**: Implement `Flag` interface
2. **Region Validators**: Pre-process region creation
3. **Storage Adapters**: Support custom databases
4. **Selection Tools**: Alternative selection methods

## 🚀 Performance Considerations

### Optimization Techniques
1. **Lazy Loading**: Regions loaded on-demand
2. **Spatial Partitioning**: Divide world into chunks for faster lookup
3. **Caching**: Frequently accessed regions and flag evaluations
4. **Batch Operations**: Bulk region updates
5. **Async Processing**: Offload heavy operations

### Memory Management
- **Region Objects**: Lightweight, flyweight pattern for common data
- **Flag Values**: Enum-based for memory efficiency
- **Cache Limits**: Configurable size limits for caches
- **Soft References**: Allow GC to reclaim memory under pressure

## 🔒 Security Considerations

### Permission System
- **Fine-grained Permissions**: Per-command, per-region, per-flag
- **Inheritance**: Permission inheritance from parent groups
- **Temporary Permissions**: Time-limited access grants
- **Audit Logging**: Track permission changes

### Input Validation
- **Region Names**: Sanitize and validate
- **Coordinate Bounds**: Prevent invalid regions
- **Flag Values**: Type checking and range validation
- **SQL Injection**: Parameterized queries

## 📈 Scalability

### Horizontal Scaling
- **Database Centralization**: Shared MySQL for multi-server
- **Cache Synchronization**: Redis for distributed caching
- **Load Distribution**: Region-based partitioning

### Vertical Scaling
- **Memory Optimization**: Efficient data structures
- **CPU Optimization**: Parallel processing where safe
- **I/O Optimization**: Async database operations

## 🛠️ Development Guidelines

### Code Organization
```
com.worldprotect/
├── api/          # Public API interfaces
├── core/         # Core business logic
├── impl/         # Implementation details
├── util/         # Shared utilities
└── platform/     # Platform-specific code (Paper/Folia)
```

### Testing Strategy
- **Unit Tests**: Isolated component testing
- **Integration Tests**: Component interaction testing
- **Performance Tests**: Load and stress testing
- **Concurrency Tests**: Thread safety validation

---

*This architecture document will be updated as the plugin evolves. Refer to `DEVELOPMENT_NOTES.md` for implementation details and decisions.*