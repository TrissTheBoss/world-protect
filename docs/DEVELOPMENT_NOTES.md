# Development Notes

Internal technical documentation, design decisions, and implementation notes for World Protect developers.

## 🏗️ Architecture Decisions

### Build System Choice: Gradle over Maven
**Decision**: Use Gradle with Kotlin DSL
**Rationale**:
- Better performance with incremental builds
- Strong Paper/Folia community support via `paperweight` plugin
- Flexible dependency management
- Kotlin DSL provides type-safe build scripts
- Excellent IDE integration with VS Code

**Trade-offs**:
- Steeper learning curve than Maven
- Larger build files
- Requires Gradle wrapper for consistency

### Package Structure
```
com.worldprotect/
├── api/           # Public API interfaces
├── core/          # Core business logic
│   ├── regions/   # Region management
│   ├── flags/     # Flag system
│   ├── commands/  # Command handling
│   └── selection/ # Selection tools
├── platform/      # Platform-specific code
│   ├── paper/     # Paper-specific implementations
│   └── folia/     # Folia-specific implementations
├── storage/       # Data persistence
├── util/          # Shared utilities
└── config/        # Configuration handling
```

### Threading Model
**Decision**: Thread-safe design with Folia compatibility
**Implementation**:
- All shared data structures use concurrent collections
- ReadWriteLock for spatial index access
- Async operations for I/O (database, file system)
- Event processing with configurable thread pools

**Paper vs Folia**:
- **Paper**: Single-threaded, simple synchronization
- **Folia**: Region-scheduled threads, requires careful locking
- **Abstraction**: `PlatformScheduler` interface for both

## 🚀 Performance Optimizations

### Spatial Indexing
**Decision**: Grid-based spatial index
**Rationale**:
- Simple implementation with good performance
- Predictable memory usage
- Easy to debug and visualize
- Configurable cell size for tuning

**Implementation**:
```java
public class GridSpatialIndex implements SpatialIndex {
    private final Map<ChunkCoordinate, Set<Region>> grid;
    private final int cellSize; // chunks
    
    public List<Region> getRegionsAt(Location location) {
        ChunkCoordinate coord = toGridCoordinate(location);
        return grid.getOrDefault(coord, Collections.emptySet());
    }
}
```

### Caching Strategy
**Layers**:
1. **Region Cache**: LRU cache for frequently accessed regions
2. **Flag Cache**: Computed flag values for location-flag pairs
3. **Spatial Cache**: Pre-computed region lists for grid cells

**Cache Invalidation**:
- Time-based expiration
- Size-based eviction
- Manual invalidation on region changes

### Event Processing
**Optimizations**:
- Early return on irrelevant events
- Async processing for expensive operations
- Event priority tuning (HIGH for critical protections)
- Event filtering (ignore creative mode, OPs)

## 🗄️ Storage Design

### Database Schema
**Decision**: SQL with optional YAML fallback
**Rationale**:
- SQLite for single-server deployments
- MySQL for multi-server setups
- YAML for simple testing/development

**Schema Evolution**:
- Flyway migrations for schema updates
- Version tracking in database
- Automatic migration on plugin update
- Backup before migration

### Data Models
```java
public class RegionData {
    private String name;
    private String world;
    private Vector min;
    private Vector max;
    private int priority;
    private Map<Flag, FlagValue> flags;
    private Set<UUID> members;
    private UUID owner;
    private Instant createdAt;
    private Instant updatedAt;
}
```

## 🔌 Plugin API Design

### Public API Principles
1. **Stability**: Breaking changes only in major versions
2. **Simplicity**: Intuitive method names and parameters
3. **Completeness**: Cover all essential operations
4. **Extensibility**: Support custom flags and validators

### API Surface
```java
public interface WorldProtectAPI {
    // Region operations
    Optional<Region> getRegion(String name);
    List<Region> getRegionsAt(Location location);
    Region createRegion(String name, Location min, Location max);
    boolean deleteRegion(String name);
    
    // Flag operations
    boolean isAllowed(Location location, Flag flag);
    void setFlag(Region region, Flag flag, FlagValue value);
    FlagValue getFlag(Region region, Flag flag);
    
    // Event hooks
    void registerFlagHandler(FlagHandler handler);
    void registerRegionValidator(RegionValidator validator);
    
    // Utility methods
    WorldProtect getPlugin();
    Configuration getConfiguration();
}
```

### Extension Points
1. **Custom Flags**: Implement `Flag` interface
2. **Region Validators**: Validate region creation/modification
3. **Storage Adapters**: Support custom databases
4. **Selection Tools**: Alternative selection methods

## 🧪 Testing Strategy

### Test Categories
1. **Unit Tests**: Isolated component testing
2. **Integration Tests**: Component interaction testing
3. **Performance Tests**: Load and stress testing
4. **Concurrency Tests**: Thread safety validation

### Test Infrastructure
```java
@ExtendWith(MockitoExtension.class)
class RegionManagerTest {
    @Mock World world;
    @Mock RegionStorage storage;
    @InjectMocks RegionManager regionManager;
    
    @Test
    void createRegion_validSelection_createsRegion() {
        // Test implementation
    }
}
```

### Mock Server
```java
public class MockServerExtension implements BeforeEachCallback {
    private MockServer server;
    
    @Override
    public void beforeEach(ExtensionContext context) {
        server = MockServer.create();
        // Setup mock server
    }
}
```

## 🔧 Development Tools

### VS Code Configuration
**Recommended Extensions**:
1. **Extension Pack for Java** - Comprehensive Java support
2. **Gradle for Java** - Gradle tasks integration
3. **GitLens** - Enhanced Git history
4. **Markdown All in One** - Documentation authoring
5. **YAML** - Configuration file editing

**Settings**:
```json
{
  "java.compile.nullAnalysis.mode": "automatic",
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.debug.settings.onBuildFailureProceed": true,
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": "always"
  }
}
```

### Build Script Features
**Gradle Tasks**:
- `build` - Compile and run tests
- `runServer` - Start development server
- `shadowJar` - Create distributable JAR
- `test` - Run unit tests
- `jacocoTestReport` - Generate coverage report

**Dependency Management**:
- Paper/Folia API via `paperweight`
- Configuration library (BoostedYAML)
- Testing libraries (JUnit 5, Mockito)
- Metrics (bStats)

## 📚 Documentation System

### Auto-Generated Documentation
**Sources**:
1. **Command Registry**: Generates `COMMANDS_AND_PERMISSIONS.md`
2. **Flag Enum**: Generates `FLAGS.md`
3. **Configuration Classes**: Generates `CONFIGURATION.md`

**Process**:
```java
public class DocumentationGenerator {
    public void generateCommandDocs(CommandRegistry registry) {
        // Extract command metadata
        // Generate Markdown tables
        // Update documentation file
    }
}
```

### Documentation Sync
**Manual Steps**:
1. Update Java source code
2. Run documentation generation
3. Review generated content
4. Commit changes

**Automation**:
- GitHub Actions workflow for doc generation
- Pre-commit hooks for validation
- CI checks for documentation consistency

## 🚢 Deployment Pipeline

### CI/CD Workflow
**Stages**:
1. **Build**: Compile and run tests
2. **Test**: Integration and performance tests
3. **Package**: Create distributable JAR
4. **Deploy**: Upload to GitHub Releases

**Artifacts**:
- `WorldProtect-{version}.jar` - Main plugin JAR
- `WorldProtect-{version}-sources.jar` - Source code
- `WorldProtect-{version}-javadoc.jar` - API documentation

### Versioning Strategy
**Semantic Versioning**:
- **MAJOR**: Breaking API changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

**Pre-release Tags**:
- `-alpha.{n}` - Early testing
- `-beta.{n}` - Feature complete, bug fixing
- `-rc.{n}` - Release candidate

## 🔒 Security Considerations

### Input Validation
**Validation Points**:
1. **Region Names**: Regex validation, length limits
2. **Coordinates**: Boundary checks, world validation
3. **Flag Values**: Enum validation, type checking
4. **Player Input**: UUID validation, permission checks

### Permission System
**Design**:
- Fine-grained permission nodes
- Permission inheritance
- Temporary permissions
- Audit logging

**Implementation**:
```java
public class PermissionManager {
    public boolean checkPermission(CommandSender sender, String permission) {
        // Check permission with caching
        // Support for Vault/LuckPerms integration
    }
}
```

### Data Protection
**Measures**:
- SQL injection prevention (parameterized queries)
- File system permission checks
- Configuration file validation
- Backup encryption (optional)

## 📈 Performance Monitoring

### Metrics Collection
**bStats Integration**:
- Plugin version distribution
- Server software versions
- Feature usage statistics
- Performance metrics

**Custom Metrics**:
- Region count and distribution
- Event processing times
- Cache hit rates
- Database query performance

### Profiling Tools
**JVM Profiling**:
- VisualVM for CPU/memory profiling
- Async Profiler for production profiling
- JFR (Java Flight Recorder) for detailed analysis

**Plugin-Specific**:
- `/wp debug metrics` - Show performance metrics
- `/wp debug profile` - Start/stop profiling
- `/wp debug gc` - Trigger garbage collection

## 🐛 Debugging and Troubleshooting

### Debug Commands
```bash
# Enable debug mode
/wp debug on

# Show performance metrics
/wp debug metrics

# Test region lookup
/wp debug test-lookup <x> <y> <z>

# Dump region data
/wp debug dump-regions

# Clear caches
/wp debug clear-cache
```

### Logging Strategy
**Log Levels**:
- `DEBUG`: Detailed development information
- `INFO`: Normal operational messages
- `WARN`: Potential issues
- `ERROR`: Critical failures

**Log Categories**:
- `REGION`: Region creation/deletion/modification
- `FLAG`: Flag evaluation and changes
- `STORAGE`: Database operations
- `PERFORMANCE`: Performance-related events

### Common Issues
**Memory Leaks**:
- Symptom: Increasing memory usage over time
- Solution: Check cache sizes, review object references

**Performance Degradation**:
- Symptom: Lag spikes during region operations
- Solution: Optimize spatial index, tune cache settings

**Database Issues**:
- Symptom: Region data not saving/loading
- Solution: Check database connection, run migrations

## 🔮 Future Enhancements

### Planned Features
1. **Polygonal Regions**: Support for complex shapes
2. **Region Groups**: Logical grouping of regions
3. **Web Interface**: Browser-based region management
4. **Advanced Flags**: Time-based, condition-based flags
5. **WorldEdit Integration**: Seamless selection import/export

### Technical Improvements
1. **Alternative Spatial Indexes**: R-tree, Quadtree implementations
2. **Distributed Caching**: Redis support for multi-server
3. **Advanced Profiling**: Built-in performance analysis tools
4. **Plugin Marketplace**: In-game plugin extensions

### API Expansion
1. **Event System**: More granular event hooks
2. **Condition System**: Complex flag conditions
3. **Scripting Support**: JavaScript/Lua scripting for custom logic
4. **Webhook Integration**: Notifications for region changes

## 📝 Code Style Guidelines

### Java Conventions
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: 120 characters maximum
- **Braces**: K&R style (opening brace on same line)
- **Naming**: camelCase for methods/variables, PascalCase for classes
- **Annotations**: Use `@Nullable` and `@NotNull` where appropriate

### Documentation Standards
- **Javadoc**: All public APIs must have Javadoc
- **Comments**: Explain why, not what (except complex algorithms)
- **Examples**: Include usage examples in documentation
- **TODOs**: Use `// TODO:` with issue reference if available

### Testing Standards
- **Test Coverage**: Aim for 80%+ coverage on critical paths
- **Test Naming**: `methodName_scenario_expectedResult`
- **Test Isolation**: Each test should be independent
- **Assertions**: Use descriptive assertion messages

---

*This document is maintained by the development team. Update when making significant architectural changes or adding new features.*