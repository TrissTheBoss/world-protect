# Git Setup Verification Checklist

Use this checklist to verify that the World Protect project is correctly connected to GitHub and ready for development.

## ✅ Prerequisites Verification

- [ ] **Git installed** on system (check with `git --version`)
- [ ] **GitHub account** linked in VS Code
- [ ] **GitHub repository** created at `https://github.com/<YOUR-ACCOUNT>/world-protect`
- [ ] **VS Code** with Git extension installed

## 🔧 Initial Git Setup (If Not Already Done)

### 1. Initialize Git Repository
```bash
git init
```

### 2. Configure Git User
```bash
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

### 3. Add All Files
```bash
git add .
```

### 4. Initial Commit
```bash
git commit -m "chore: initial World Protect scaffolding"
```

### 5. Add GitHub Remote
```bash
git remote add origin https://github.com/<YOUR-ACCOUNT>/world-protect.git
```

### 6. Push to GitHub
```bash
git push -u origin main
```

## 📋 Verification Steps

### VS Code Source Control View
- [ ] Repository shows as "WORLD PROTECT" in Source Control panel
- [ ] Current branch shows as `main`
- [ ] No uncommitted changes shown
- [ ] Remote shows as `origin`

### Git Status Check
```bash
git status
```
- [ ] Output shows "On branch main"
- [ ] Output shows "Your branch is up to date with 'origin/main'"
- [ ] Output shows "nothing to commit, working tree clean"

### Remote Verification
```bash
git remote -v
```
- [ ] Shows `origin` pointing to your GitHub repository
- [ ] URL format: `https://github.com/<YOUR-ACCOUNT>/world-protect.git`

### File Verification
- [ ] `.gitignore` file exists and excludes build files
- [ ] `.github/workflows/build.yml` exists
- [ ] All documentation files are present in `docs/`

## 🚀 Test Workflow

### 1. Create Test Branch
```bash
git checkout -b test/workflow-verification
```

### 2. Make Small Change
Edit any file (e.g., add a comment to `README.md`)

### 3. Stage and Commit
```bash
git add .
git commit -m "test: verify Git workflow"
```

### 4. Push Branch
```bash
git push -u origin test/workflow-verification
```

### 5. Verify on GitHub
- [ ] Visit GitHub repository
- [ ] See new branch `test/workflow-verification`
- [ ] See the test commit

### 6. Cleanup
```bash
git checkout main
git branch -d test/workflow-verification
git push origin --delete test/workflow-verification
```

## 📚 Documentation Verification

- [ ] `docs/GIT_AND_RELEASE_WORKFLOW.md` exists and is complete
- [ ] `CONTRIBUTING.md` references Git workflow
- [ ] `README.md` includes Git workflow in documentation list
- [ ] All links in documentation work

## 🔄 Continuous Integration Check

- [ ] `.github/workflows/build.yml` is committed
- [ ] GitHub Actions enabled for repository
- [ ] Workflow runs on push to `main`
- [ ] Workflow runs on pull requests

## ⚙️ Configuration Files

### Version Synchronization
- [ ] `gradle.properties` has `pluginVersion=1.0.0-SNAPSHOT`
- [ ] `gradle.properties` has `version=1.0.0-SNAPSHOT`
- [ ] `plugin.yml` uses `${pluginVersion}` variable

### Build Configuration
- [ ] `build.gradle.kts` compiles without errors
- [ ] `settings.gradle.kts` sets correct project name
- [ ] `.gitignore` excludes build artifacts

## 🐛 Troubleshooting

### Common Issues and Solutions

**Issue**: "Git not recognized"
**Solution**: Install Git from https://git-scm.com/

**Issue**: "Permission denied to GitHub"
**Solution**:
1. Use HTTPS with credentials helper:
   ```bash
   git config --global credential.helper manager
   ```
2. Or use SSH keys:
   ```bash
   git remote set-url origin git@github.com:<YOUR-ACCOUNT>/world-protect.git
   ```

**Issue**: "Remote origin already exists"
**Solution**:
```bash
git remote remove origin
git remote add origin https://github.com/<YOUR-ACCOUNT>/world-protect.git
```

**Issue**: "Failed to push some refs"
**Solution**:
```bash
git pull origin main --rebase
git push origin main
```

## 📈 Next Steps After Verification

1. **Enable Branch Protection** (optional but recommended):
   - Go to GitHub repository → Settings → Branches
   - Add branch protection rule for `main`
   - Require pull request reviews
   - Require status checks

2. **Set Up GitHub Secrets** (for future CI/CD):
   - Repository secrets for deployment
   - Environment variables

3. **Configure GitHub Projects** (optional):
   - Create project board
   - Set up issue templates
   - Configure labels

4. **First Real Development**:
   ```bash
   git checkout -b feature/first-feature
   # Start implementing plugin features
   ```

## 🔗 Useful Commands Reference

### Basic Git Commands
```bash
# Status and information
git status
git log --oneline
git remote -v

# Branch operations
git branch
git checkout -b feature/name
git checkout main
git branch -d branch-name

# Commit and push
git add .
git commit -m "message"
git push origin branch-name

# Sync with remote
git pull origin main
git fetch --all
```

### VS Code Git Integration
- **Stage Changes**: Click `+` next to files in Source Control
- **Commit**: Enter message, press Ctrl+Enter
- **Push**: Click "..." → Push
- **Pull**: Click "..." → Pull
- **Sync**: Click "Sync Changes" button

## ✅ Final Verification

After completing all checks:
- [ ] All verification steps completed
- [ ] Git repository initialized and connected to GitHub
- [ ] All files committed and pushed
- [ ] Documentation updated and linked
- [ ] Ready for plugin development

---

*This verification ensures that the World Protect project has a solid Git foundation for collaborative development and reliable version control.*