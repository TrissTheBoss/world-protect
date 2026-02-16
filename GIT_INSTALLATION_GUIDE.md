# World Protect – Git Installation & Connection Guide

This guide provides step-by-step instructions to install Git, configure it for the World Protect project, and connect your local VS Code workspace to GitHub.

## 🚀 Quick Start Overview

1. **Install Git** on your operating system
2. **Configure Git** with your identity
3. **Connect VS Code project** to GitHub repository
4. **Verify setup** matches the defined workflow
5. **Run sanity test** before starting development

## 1. Install & Configure Git

### Check if Git is Already Installed

Open a terminal (Command Prompt, PowerShell, Terminal, or VS Code Integrated Terminal) and run:

```bash
git --version
```

**Expected output**: `git version 2.x.x` or similar. If you see a version number, Git is installed. If you see "command not found" or similar, proceed with installation.

### Windows Installation

#### Option A: Official Installer (Recommended)
1. Download the latest Git for Windows installer from: https://git-scm.com/download/win
2. Run the installer with these recommended settings:
   - **Select Components**: Check "Git Bash Here", "Git GUI Here", "Associate .git* files with Git Bash"
   - **Default Editor**: Choose "Use Visual Studio Code as Git's default editor"
   - **PATH Environment**: Select "Git from the command line and also from 3rd-party software"
   - **SSH Executable**: Choose "Use OpenSSH"
   - **Line Endings**: Select "Checkout Windows-style, commit Unix-style line endings"
   - **Terminal Emulator**: Choose "Use MinTTY"
3. Complete the installation

#### Option B: Using Winget (Windows Package Manager)
```powershell
winget install --id Git.Git -e --source winget
```

#### Verify Windows Installation
```bash
git --version
```

### macOS Installation

#### Option A: Official Installer
1. Download the latest Git for macOS installer from: https://git-scm.com/download/mac
2. Open the downloaded `.dmg` file
3. Run the installer package
4. Follow the installation wizard

#### Option B: Using Homebrew (if installed)
```bash
brew install git
```

#### Verify macOS Installation
```bash
git --version
```

### Linux (Debian/Ubuntu) Installation

```bash
sudo apt-get update
sudo apt-get install git
```

#### Verify Linux Installation
```bash
git --version
```

## 2. Configure Git Identity

After installation, configure your global Git identity (required for commits):

```bash
git config --global user.name "Your Name"
git config --global user.email "your-email@example.com"
```

### Recommended Additional Configuration

```bash
# Set main as default branch (modern convention)
git config --global init.defaultBranch main

# Use merge strategy (easier for beginners)
git config --global pull.rebase false

# Use VS Code as default editor
git config --global core.editor "code --wait"

# Enable color output
git config --global color.ui auto

# Set line ending handling (Windows users)
git config --global core.autocrlf true

# Set line ending handling (macOS/Linux users)
git config --global core.autocrlf input
```

### Verify Configuration

```bash
git config --list
```

You should see your name, email, and other configured settings.

## 3. (Optional) SSH Setup for GitHub

Using SSH keys provides secure, password-less authentication. Recommended for frequent GitHub users.

### Generate SSH Key

```bash
# Generate a new SSH key (use your GitHub email)
ssh-keygen -t ed25519 -C "your-email@example.com"

# Or if your system doesn't support ed25519:
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
```

When prompted:
- Press Enter to accept the default file location
- Enter a secure passphrase (optional but recommended)

### Add SSH Key to SSH Agent

```bash
# Start the SSH agent
eval "$(ssh-agent -s)"

# Add your SSH private key
ssh-add ~/.ssh/id_ed25519
# or
ssh-add ~/.ssh/id_rsa
```

### Add SSH Key to GitHub

1. Copy your public key to clipboard:
   ```bash
   # Windows (PowerShell)
   cat ~/.ssh/id_ed25519.pub | clip
   
   # macOS
   pbcopy < ~/.ssh/id_ed25519.pub
   
   # Linux
   cat ~/.ssh/id_ed25519.pub | xclip -selection clipboard
   ```

2. Go to GitHub: https://github.com/settings/keys
3. Click "New SSH key"
4. Paste your key, give it a title (e.g., "My Laptop")
5. Click "Add SSH key"

### Test SSH Connection

```bash
ssh -T git@github.com
```

Expected output: `Hi username! You've successfully authenticated...`

## 4. Connect VS Code Project to GitHub Repository

### Prerequisites
- World Protect project folder open in VS Code
- GitHub repository created at: `https://github.com/<MY-ACCOUNT>/world-protect`
- Git installed and configured

### Step 1: Initialize Local Repository

#### Using VS Code UI:
1. Open VS Code Source Control panel (Ctrl+Shift+G)
2. Click "Initialize Repository" button
3. VS Code will create `.git` folder

#### Using Terminal:
```bash
# Navigate to project directory
cd "c:\Users\triss\Desktop\World Protect"

# Initialize Git repository
git init

# Add all files
git add .

# Create initial commit
git commit -m "chore: initial World Protect scaffolding"
```

### Step 2: Add GitHub Remote

#### Using HTTPS (Simpler, requires authentication each time):
```bash
git remote add origin https://github.com/<MY-ACCOUNT>/world-protect.git
```

#### Using SSH (Recommended for frequent use):
```bash
git remote add origin git@github.com:<MY-ACCOUNT>/world-protect.git
```

**Replace `<MY-ACCOUNT>` with your GitHub username**

### Step 3: Push to GitHub

```bash
# Push and set upstream
git push -u origin main
```

If prompted for credentials:
- **HTTPS**: Enter GitHub username and Personal Access Token (not password)
- **SSH**: No prompt if SSH key is configured

#### Using VS Code UI:
1. In Source Control panel, click "..." (More Actions)
2. Select "Push"
3. If no remote exists, VS Code will prompt to add one

### Step 4: Verify Connection

```bash
# Check remote configuration
git remote -v

# Check status
git status

# View commit history
git log --oneline
```

**Expected output**:
- `origin` points to your GitHub repository
- Status shows "Your branch is up to date with 'origin/main'"
- Log shows your initial commit

## 5. Align with Existing Workflow Docs

### From `VERIFY_GIT_SETUP.md`:
**Perform these sections NOW:**
1. **Prerequisites Verification** (all checks)
2. **Initial Git Setup** (steps 1-6)
3. **Verification Steps** (VS Code, Git Status, Remote Verification)
4. **File Verification** (check all files exist)

**Save these for LATER:**
- Test Workflow (section 3) - Do after sanity test
- Continuous Integration Check - Do after first push
- Next Steps After Verification - Reference as needed

### From `docs/GIT_AND_RELEASE_WORKFLOW.md`:
**Focus on these sections NOW:**
1. **Repository State Verification** - Confirm your setup matches
2. **Always Sync with GitHub** - Understand the daily workflow
3. **Branching Model** - Know how to create feature branches

**Reference these LATER:**
- Versioning Strategy - When ready for first release
- Release Workflow - When preparing version 1.0.0
- Handling Conflicts - When you encounter merge conflicts
- GitHub Actions CI/CD - Already configured in `.github/workflows/`

## 6. Versioning and Branch Check

### Verify Default Branch
```bash
git branch
```
Should show `* main`

### Check Project Version
```bash
# Check Gradle version
cat gradle.properties | grep version

# Check plugin version
cat gradle.properties | grep pluginVersion
```

**Expected**: `pluginVersion=1.0.0-SNAPSHOT` and `version=1.0.0-SNAPSHOT`

### Verify Plugin Metadata
```bash
# Check plugin.yml uses version variable
cat src/main/resources/plugin.yml | grep version
```

**Expected**: `version: ${pluginVersion}`

## 7. Sanity Test Before Development

Run this quick test to ensure everything works:

### Step 1: Create Test Branch
```bash
git checkout -b test/sanity-check
```

### Step 2: Make Small Change
Edit `README.md` and add a test comment at the bottom:
```markdown
<!-- Sanity test: Git workflow verification -->
```

### Step 3: Stage and Commit
```bash
git add README.md
git commit -m "test: verify Git workflow with sanity check"
```

### Step 4: Push to GitHub
```bash
git push -u origin test/sanity-check
```

### Step 5: Verify on GitHub
1. Go to: `https://github.com/<MY-ACCOUNT>/world-protect`
2. Check:
   - New branch `test/sanity-check` exists
   - New commit appears in branch
   - README.md shows your test comment

### Step 6: Cleanup
```bash
# Switch back to main
git checkout main

# Delete local test branch
git branch -d test/sanity-check

# Delete remote test branch
git push origin --delete test/sanity-check

# Pull any changes (should be none)
git pull origin main
```

### Step 7: Final Verification
```bash
git status
```
**Expected**: "Your branch is up to date with 'origin/main'. Nothing to commit, working tree clean"

## 8. Troubleshooting Common Issues

### Issue: "Git not recognized"
**Solution**: Ensure Git is in PATH. Restart VS Code after installation.

### Issue: "Permission denied" (HTTPS)
**Solution**: Use Personal Access Token instead of password:
1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with "repo" scope
3. Use token as password when prompted

### Issue: "Remote origin already exists"
**Solution**:
```bash
git remote remove origin
git remote add origin https://github.com/<MY-ACCOUNT>/world-protect.git
```

### Issue: "Failed to push some refs"
**Solution**:
```bash
git pull origin main --rebase
git push origin main
```

### Issue: VS Code doesn't detect Git
**Solution**:
1. Restart VS Code
2. Check `git --version` works in VS Code terminal
3. Ensure project folder contains `.git` directory

## 9. Ready for Development

After completing all steps, you have:

✅ **Git installed and configured**  
✅ **Project connected to GitHub**  
✅ **Workflow aligned with documentation**  
✅ **Sanity test passed**  
✅ **Ready for plugin implementation**

### Next Steps:
1. **Start Prompt 2**: Begin implementing World Protect plugin features
2. **Use feature branches**: `git checkout -b feature/your-feature`
3. **Follow commit conventions**: Use Conventional Commits format
4. **Regular sync**: Pull before work, push after work
5. **Reference docs**: Consult `docs/GIT_AND_RELEASE_WORKFLOW.md` as needed

### Quick Reference Commands:
```bash
# Daily workflow
git pull origin main
git checkout -b feature/name
# ... make changes ...
git add .
git commit -m "type(scope): description"
git push -u origin feature/name

# Create pull request on GitHub
# After PR merge:
git checkout main
git pull origin main
git branch -d feature/name
```

---

*Your World Protect project is now fully configured with Git and GitHub, ready for collaborative development following the established workflow.*