# Changelog

All notable changes to World Protect will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure and documentation
- Gradle build system with Paper/Folia support
- Basic plugin skeleton with placeholder classes
- Comprehensive documentation framework

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

### Security
- N/A

## [1.0.0] - Planned Release

### Planned Features
- Region creation, deletion, and management
- Comprehensive flag system (50+ protection flags)
- Visual selection tools with wand
- Priority-based region overlap handling
- SQLite and MySQL storage support
- Full Folia compatibility
- Permission system integration
- Configuration GUI (web-based optional)
- API for developers
- Import/export functionality
- WorldEdit compatibility layer

---

## Versioning Scheme

- **MAJOR** version for incompatible API changes
- **MINOR** version for new functionality in a backward compatible manner  
- **PATCH** version for backward compatible bug fixes

## Release Process

1. Create release branch from `main`
2. Update version in `gradle.properties` and `plugin.yml`
3. Run full test suite
4. Update CHANGELOG.md with release notes
5. Create GitHub release with tag `vX.Y.Z`
6. Merge release branch back to `main`
7. Deploy to GitHub Packages/Maven repository

## Backward Compatibility

World Protect follows these compatibility guidelines:

- **API**: Breaking changes only in MAJOR versions
- **Configuration**: Automatic migration for minor changes
- **Database**: Migration scripts provided for schema changes
- **Commands**: Deprecated commands remain functional for one major version

## Upgrade Notes

When upgrading between versions:

1. Always backup your `plugins/WorldProtect/` directory
2. Check the changelog for breaking changes
3. Test in a staging environment first
4. Follow any migration instructions provided

---

*This changelog format was inspired by [Keep a Changelog](https://keepachangelog.com/).*