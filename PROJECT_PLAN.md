# World Protect – Project & Documentation Plan

## High-Level Checklist

- [x] Analyze requirements and create project plan
- [ ] Design repository structure and build system
- [ ] Create documentation file skeletons
- [ ] Set up VS Code tooling recommendations
- [ ] Design CI/CD workflow
- [ ] Create all necessary files and directories
- [ ] Verify the complete setup

## 1. Build System Choice: Gradle

**Rationale:** Gradle is the modern standard for Minecraft plugin development due to:
- Better performance with incremental builds
- Flexible dependency management
- Strong Paper/Folia community support via plugins like `io.papermc.paperweight.userdev`
- Kotlin DSL for type-safe build scripts
- Excellent IDE integration with VS Code

## 2. Proposed Repository Structure

```
World-Protect/
├── .github/
│   └── workflows/
│       └── build.yml
├── docs/
│   ├── ARCHITECTURE.md
│   ├── COMMANDS_AND_PERMISSIONS.md
│   ├── CONFIGURATION.md
│   ├── DEVELOPMENT_NOTES.md
│   ├── FLAGS.md
│   └── REGIONS_AND_SELECTIONS.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── worldprotect/
│   │   │           ├── WorldProtectPlugin.java
│   │   │           ├── commands/
│   │   │           ├── config/
│   │   │           ├── flags/
│   │   │           ├── regions/
│   │   │           └── selection/
│   │   └── resources/
│   │       ├── plugin.yml
│   │       └── config.yml
│   └── test/
│       └── java/
│           └── com/
│               └── worldprotect/
├── .gitignore
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
├── README.md
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## 3. Java and Minecraft Targets

- **Java Version:** 17 (minimum required by Paper 1.17+)
- **Paper API:** 1.20.4 (latest stable)
- **Folia Support:** Yes, via Paper's Folia API
- **Gradle Version:** 8.5+ with wrapper

## 4. Documentation Sync Strategy

To keep documentation in sync with code:

1. **Command/Permission Registry:** All commands will be registered in a central `CommandRegistry` class with annotations. The AI will parse this class to generate `COMMANDS_AND_PERMISSIONS.md`.

2. **Flag Enum:** All protection flags will be defined in a `Flag` enum with metadata (name, description, default). The AI will reflect this enum to generate `FLAGS.md`.

3. **Configuration Classes:** Config options will be defined in `@ConfigSerializable` classes. The AI will reflect these to generate `CONFIGURATION.md`.

4. **Sync Process:** When asked to update documentation, the AI will:
   - Read the relevant Java source files
   - Extract structured data using regex or AST analysis
   - Regenerate the corresponding Markdown sections
   - Preserve any manual explanatory text outside the auto-generated sections

## 5. VS Code Tooling Recommendations

### Essential Extensions:
1. **Extension Pack for Java** (`vscjava.vscode-java-pack`) - Comprehensive Java support
2. **Gradle for Java** (`vscjava.vscode-gradle`) - Gradle tasks integration
3. **GitLens** (`eamodio.gitlens`) - Enhanced Git history and blame
4. **GitHub Pull Requests** (`GitHub.vscode-pull-request-github`) - PR/issue management
5. **Markdown All in One** (`yzhang.markdown-all-in-one`) - Markdown authoring
6. **YAML** (`redhat.vscode-yaml`) - YAML syntax for config files
7. **Error Lens** (`usernamehw.errorlens`) - Inline error highlighting

### AI Workflow:
- The AI will have full workspace context via the VS Code extension
- When asked to update docs: read source → generate markdown → write file
- Use inline completions for boilerplate code patterns
- Leverage code actions for refactoring and test generation

## 6. CI/CD Workflow

GitHub Actions workflow will:
1. Checkout code
2. Setup JDK 17
3. Run Gradle build with tests
4. Cache Gradle dependencies
5. (Future) Publish to GitHub Releases

## 7. Branching & Release Conventions

- **Main Branch:** `main` (stable releases)
- **Development Branch:** `develop` (optional)
- **Feature Branches:** `feature/description`
- **Release Tags:** `v1.0.0`, `v1.1.0-rc.1`
- **Commit Messages:** Conventional Commits format
- **Artifacts:** `WorldProtect-{version}.jar` in GitHub Releases

---

**Next Steps:** Create the actual files and directories according to this plan.