# Git and Release Workflow

This document describes the Git and GitHub workflow for World Protect development, including versioning, branching, release processes, and synchronization between local VS Code and GitHub.

## 📋 Repository State Verification

### Current State in VS Code
In the VS Code Source Control view (Ctrl+Shift+G), you should see:

1. **Repository Indicator**: "WORLD PROTECT" in the Source Control panel
2. **Changes Section**: Lists modified, staged, and untracked files
3. **Branch Indicator**: Shows current branch (e.g., `main`)
4. **Sync Status**: Shows sync status with remote (up to date, behind, ahead)
5. **Remote Indicator**: Shows remote name (typically `origin`)

### If Repository is Not Initialized
If the folder is not yet a Git repository:

1. **Initialize Repository**:
   ```bash
   git init
   ```

2. **Add All Files**:
   ```bash
   git add .
   ```

3. **Make Initial Commit**:
   ```bash
   git commit -m "chore: initial World Protect scaffolding"
   ```

4. **Add GitHub Remote**:
   ```bash
   git remote add origin https://github.com/<YOUR-ACCOUNT>/world-protect.git
   ```

5. **Push to GitHub**:
   ```bash
   git push -u origin main
   ```

### Verify Remote Configuration
Check current remote URL:
```bash
git remote -v
```

Update remote if incorrect:
```bash
git remote set-url origin https://github.com/<YOUR-ACCOUNT>/world-protect.git
```

## 🏷️ Versioning Strategy (SemVer + Tags)

### Semantic Versioning
World Protect follows **Semantic Versioning 2.0.0**:

- **MAJOR version** (X.0.0): Incompatible API changes
- **MINOR version** (0.X.0): New functionality in backward compatible manner
- **PATCH version** (0.0.X): Backward compatible bug fixes

**Version Bump Rules**:
- **MAJOR**: Breaking plugin API changes, major architecture changes
- **MINOR**: New features, new flags, new commands
- **PATCH**: Bug fixes, performance improvements, documentation updates

### Version Synchronization
The project version must always match the latest Git tag:

1. **Version Location**: `gradle.properties` (`pluginVersion` and `version`)
2. **Plugin Metadata**: `plugin.yml` (auto-expanded from Gradle properties)
3. **Git Tag Format**: `vX.Y.Z` (e.g., `v1.0.0`)

### Release Workflow
1. **Update Version**:
   ```bash
   # Edit gradle.properties
   pluginVersion=1.0.0
   version=1.0.0
   ```

2. **Commit Version Change**:
   ```bash
   git add gradle.properties
   git commit -m "chore(release): v1.0.0"
   ```

3. **Create Annotated Tag**:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   ```

4. **Push to GitHub**:
   ```bash
   git push origin main --tags
   ```

5. **GitHub Release** (manual or automated):
   - Go to GitHub repository → Releases → Draft new release
   - Select the tag `v1.0.0`
   - Add release notes from `CHANGELOG.md`
   - Attach build artifacts

### Handling Failed Changes
- **Failed Experiments**: Keep in history with clear commit messages
  ```
  git commit -m "experiment: try new region algorithm"
  git commit -m "revert: new region algorithm caused performance issues"
  ```
- **Broken Commits**: Fix with follow-up commits, don't delete history
- **Tags**: Only create for stable, tested releases

## 🌿 Branching Model

### Main Branches
- **`main`**: Stable integration branch, always deployable
- **`develop`** (optional): Integration branch for features

### Feature Branches
Create feature branches for new functionality:
```bash
git checkout -b feature/region-priority
```

**Naming Conventions**:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test improvements

### Pull Request Workflow
1. **Create Feature Branch**:
   ```bash
   git checkout -b feature/new-flag
   ```

2. **Make Changes and Commit**:
   ```bash
   git add .
   git commit -m "feat: add time-based flag system"
   ```

3. **Push to GitHub**:
   ```bash
   git push -u origin feature/new-flag
   ```

4. **Create Pull Request**:
   - Go to GitHub repository
   - Click "Compare & pull request"
   - Fill PR template
   - Request reviews

5. **Merge to Main**:
   - After approval, merge via GitHub UI
   - Delete feature branch

## 🔄 Always Sync with GitHub

### Daily Workflow
1. **Before Starting Work**:
   ```bash
   git pull origin main
   ```
   or use VS Code "Sync Changes" button

2. **During Work**:
   - Make small, focused commits
   - Write descriptive commit messages
   - Follow Conventional Commits format

3. **After Work**:
   ```bash
   git push origin <branch-name>
   ```

### VS Code Source Control Panel
Use these VS Code features:

1. **Stage Changes**: Click `+` next to files
2. **Commit**: Enter message in commit box, press Ctrl+Enter
3. **Push**: Click "..." → Push
4. **Pull**: Click "..." → Pull
5. **Sync**: Click "Sync Changes" (does both pull and push)

### Commit Message Format
Follow **Conventional Commits**:
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Examples**:
```
feat(regions): add polygonal region support
fix(flags): resolve NPE in flag evaluation
docs(commands): update command examples
```

## ⚠️ Handling Conflicts

### Conflict Detection
VS Code shows conflicts in:
1. **Source Control panel**: Files with merge conflicts
2. **Editor**: Conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)

### Conflict Resolution
1. **Pull with Conflicts**:
   ```bash
   git pull origin main
   # If conflicts occur
   ```

2. **Resolve in VS Code**:
   - Open conflicted file
   - Use "Accept Current Change", "Accept Incoming Change", or edit manually
   - VS Code provides conflict resolution UI

3. **Stage Resolved Files**:
   ```bash
   git add <resolved-file>
   ```

4. **Commit Resolution**:
   ```bash
   git commit -m "Merge branch 'main' into feature/xyz"
   ```

5. **Push Changes**:
   ```bash
   git push origin feature/xyz
   ```

### Important Rules
- **Never force push** to shared branches
- **Never delete history** of failed attempts
- **Always commit conflict resolutions**
- **Keep `main` branch stable**

## 🚀 GitHub Actions CI/CD

### Workflow Files
All CI/CD configuration is in `.github/workflows/`:
- `build.yml`: Build and test workflow
- Future: `release.yml`, `deploy.yml`

### CI Rules
1. **Version Control**: All workflow files are committed to Git
2. **Broken Builds**: Fix with follow-up commits, don't delete history
3. **Branch Protection**: Consider protecting `main` branch
   - Require status checks
   - Require pull request reviews
   - Prevent force pushes

### Version-Aware Workflows
Future workflows can:
- Trigger on tags `v*.*.*`
- Extract version from `gradle.properties`
- Build and publish release artifacts
- Update GitHub Releases automatically

**Example Tag Trigger**:
```yaml
on:
  push:
    tags:
      - 'v*.*.*'
```

## 📝 Documentation Updates

### Keeping Docs in Sync
When making changes that affect:

1. **Commands**: Update `docs/COMMANDS_AND_PERMISSIONS.md`
2. **Flags**: Update `docs/FLAGS.md`
3. **Configuration**: Update `docs/CONFIGURATION.md`
4. **Workflow**: Update this document

### Documentation Commits
```bash
git add docs/
git commit -m "docs(workflow): update Git workflow guidelines"
```

## 🔧 Troubleshooting

### Common Issues

**Issue**: "Updates were rejected because the remote contains work you do not have locally"
**Solution**:
```bash
git pull origin main
# Resolve conflicts if any
git push origin main
```

**Issue**: "Permission denied to GitHub"
**Solution**:
- Check SSH keys or PAT (Personal Access Token)
- Use HTTPS with credentials helper:
  ```bash
  git config --global credential.helper manager
  ```

**Issue**: "Wrong remote URL"
**Solution**:
```bash
git remote set-url origin https://github.com/<YOUR-ACCOUNT>/world-protect.git
```

### VS Code Git Issues
1. **Refresh Git Status**: `F1` → "Git: Refresh"
2. **Clear Git Cache**: `F1` → "Git: Clear Git Cache"
3. **Reopen Folder**: Sometimes fixes Git detection

## 📊 Best Practices

### Commit Frequency
- **Small Commits**: One logical change per commit
- **Frequent Pushes**: Push at least daily
- **Meaningful Messages**: Describe why, not just what

### Branch Management
- **Delete Merged Branches**: Clean up after PR merge
- **Regular Updates**: Rebase feature branches on main weekly
- **Naming Consistency**: Follow naming conventions

### Release Discipline
- **Test Before Tag**: Ensure all tests pass
- **Update Changelog**: Before creating release
- **Communicate Changes**: Update documentation with releases

### Backup Strategy
- **GitHub as Primary**: All code on GitHub
- **Local Backups**: Regular local backups optional
- **Branch Protection**: Protect main branch

## 🔗 Related Documents
- [CONTRIBUTING.md](../CONTRIBUTING.md) - General contribution guidelines
- [DEVELOPMENT_NOTES.md](DEVELOPMENT_NOTES.md) - Technical implementation details
- [README.md](../README.md) - Project overview

---

*This workflow ensures consistent versioning, reliable releases, and smooth collaboration between local development in VS Code and the GitHub repository.*