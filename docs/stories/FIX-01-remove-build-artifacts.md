# FIX-01: Remove `.gradle/` and `.kotlin/` from Git Tracking

**Severity:** 🔴 CRITICAL  
**Size:** S (< 30 min)  
**Dependencies:** None

## Problem
17 Gradle cache files and Kotlin error logs are tracked in git despite `.gitignore` having `.gradle/` listed. These are binary blobs (some >10MB) that bloat the repo, cause merge conflicts, and contain machine-specific paths.

## Root Cause
Files were committed before `.gitignore` was added, or `.gitignore` was added after the initial commit. Git continues tracking files that were already committed even if `.gitignore` later excludes them.

## Acceptance Criteria
- [ ] AC1: `git ls-files .gradle .kotlin` returns empty (zero tracked files)
- [ ] AC2: `.gitignore` still contains `.gradle/` entry
- [ ] AC3: Add `.kotlin/` to `.gitignore` if not present
- [ ] AC4: Repo size decreases after force-push (binary blobs removed from history — optional, can skip if complex)
- [ ] AC5: `./gradlew assembleDebug` still works after removal (these are regenerated)

## Fix Steps
```bash
# 1. Remove from git tracking (keeps local files)
git rm -r --cached .gradle
git rm -r --cached .kotlin

# 2. Ensure .gitignore covers both
# Verify .gradle/ and .kotlin/ are in .gitignore

# 3. Commit
git commit -m "fix: remove .gradle and .kotlin build artifacts from git tracking"
```

## Verification
```bash
git ls-files .gradle .kotlin  # Should output nothing
git status                     # Should show nothing in .gradle/.kotlin
./gradlew assembleDebug        # Should still compile
```

## Test Requirements
None — this is a git hygiene fix.
