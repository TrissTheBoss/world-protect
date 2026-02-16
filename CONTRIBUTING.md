# Contributing to World Protect

Thank you for your interest in contributing to World Protect! This document provides guidelines and instructions for contributing to the project.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Gradle 8.5+
- Git
- VS Code (recommended) or IntelliJ IDEA

### Development Environment Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/yourusername/WorldProtect.git
   cd WorldProtect
   ```

2. **Build the Project**
   ```bash
   ./gradlew build
   ```

3. **Run Tests**
   ```bash
   ./gradlew test
   ```

4. **Run Development Server**
   ```bash
   ./gradlew runServer
   ```

### VS Code Setup
Install the recommended extensions:
- Extension Pack for Java
- Gradle for Java
- GitLens
- Markdown All in One
- YAML

## 📝 Code Style

### Java Conventions
- Follow Oracle Java Code Conventions
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use `@Nullable` and `@NotNull` annotations where appropriate
- Document public APIs with Javadoc

### Naming Conventions
- **Classes**: `PascalCase` (e.g., `RegionManager`)
- **Methods**: `camelCase` (e.g., `calculateBounds()`)
- **Variables**: `camelCase` (e.g., `playerSelection`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_REGION_SIZE`)
- **Packages**: `lowercase` (e.g., `com.worldprotect.regions`)

### Example Code Structure
```java
package com.worldprotect.regions;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a protected region in the world.
 */
public class Region {
    private final String name;
    private final Location min;
    private final Location max;
    
    /**
     * Creates a new region with the specified boundaries.
     *
     * @param name the region name
     * @param min the minimum boundary location
     * @param max the maximum boundary location
     */
    public Region(@NotNull String name, @NotNull Location min, @NotNull Location max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }
    
    // ... rest of implementation
}
```

## 🧪 Testing

### Test Structure
- Place tests in `src/test/java/`
- Use JUnit 5 for unit tests
- Use Mockito for mocking
- Follow Arrange-Act-Assert pattern

### Example Test
```java
@Test
void regionContainsLocation_shouldReturnTrueForInsideLocation() {
    // Arrange
    Region region = new Region("test", 
        new Location(world, 0, 0, 0),
        new Location(world, 10, 10, 10));
    Location inside = new Location(world, 5, 5, 5);
    
    // Act
    boolean result = region.contains(inside);
    
    // Assert
    assertTrue(result);
}
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.worldprotect.regions.RegionTest"

# Run with coverage
./gradlew jacocoTestReport
```

## 🔧 Development Workflow

For detailed Git and GitHub workflow instructions, see [Git and Release Workflow](docs/GIT_AND_RELEASE_WORKFLOW.md).

### 1. Create a Branch
```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/issue-description
```

### 2. Make Changes
- Write code following the style guide
- Add tests for new functionality
- Update documentation if needed
- Keep commits focused and atomic

### 3. Commit Messages
Use Conventional Commits format:
```
feat: add region priority system
fix: resolve NPE in region lookup
docs: update command documentation
test: add region boundary tests
chore: update dependencies
```

### 4. Run Checks Before Pushing
```bash
./gradlew check  # Runs tests, linting, and formatting
```

### 5. Create Pull Request
1. Push your branch to GitHub
2. Create a Pull Request to `main` branch
3. Fill out the PR template
4. Request review from maintainers

## 📚 Documentation

### Keeping Docs in Sync
When adding or modifying:
- **Commands**: Update `docs/COMMANDS_AND_PERMISSIONS.md`
- **Flags**: Update `docs/FLAGS.md`
- **Configuration**: Update `docs/CONFIGURATION.md`
- **API**: Update Javadoc comments

### Documentation Standards
- Use Markdown with proper headings
- Include examples for commands and configuration
- Keep tables formatted consistently
- Link between related documents

## 🐛 Issue Reporting

### Before Reporting
1. Check existing issues
2. Search documentation
3. Test with latest version

### Issue Template
```
## Description
[Clear description of the issue]

## Steps to Reproduce
1. [Step 1]
2. [Step 2]
3. [Step 3]

## Expected Behavior
[What should happen]

## Actual Behavior
[What actually happens]

## Environment
- World Protect Version: [e.g., 1.0.0]
- Server Software: [e.g., Paper 1.21.11]
- Java Version: [e.g., 17.0.9]
- OS: [e.g., Ubuntu 22.04]

## Additional Context
[Screenshots, logs, etc.]
```

## 🏗️ Project Structure

```
src/main/java/com/worldprotect/
├── WorldProtectPlugin.java      # Main plugin class
├── commands/                    # Command implementations
├── config/                      # Configuration handling
├── flags/                       # Flag definitions and logic
├── regions/                     # Region management
├── selection/                   # Selection tools
├── storage/                     # Database layer
└── util/                        # Utilities and helpers
```

## 🤝 Code Review Process

1. **Automated Checks**: CI must pass
2. **Review Criteria**:
   - Code follows style guide
   - Tests are comprehensive
   - Documentation is updated
   - No breaking changes without discussion
3. **Review Feedback**: Address all comments
4. **Merge Approval**: Requires at least one maintainer approval

## 📦 Release Process

### For Maintainers
1. Update version in `gradle.properties`
2. Update `CHANGELOG.md`
3. Create release branch
4. Run full test suite
5. Create GitHub release
6. Merge to `main`
7. Deploy to Maven repository

## ❓ Getting Help

- Check the [documentation](docs/)
- Join our [Discord server](https://discord.gg/your-invite)
- Ask in GitHub Discussions
- Contact maintainers directly

## 📄 License

By contributing, you agree that your contributions will be licensed under the project's MIT License.

---

*Thank you for contributing to World Protect!*