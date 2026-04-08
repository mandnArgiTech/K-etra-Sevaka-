# AGENTS.md

## Cursor Cloud specific instructions

### Repository overview

This is a **design/planning repository** for an Android-native, on-device AI platform (Kotlin + Jetpack Compose). No Android application source code exists yet — the repo contains architecture docs, user stories, test plans, and a **React JSX UI prototype**.

### Runnable artifact

The only runnable code is the interactive prototype at `prototype/`. It is a Vite + React app.

| Action | Command | Working directory |
|--------|---------|-------------------|
| Install deps | `npm install` | `prototype/` |
| Dev server | `npx vite --host 0.0.0.0 --port 5173` | `prototype/` |
| Lint | `npx eslint .` | `prototype/` |
| Build | `npx vite build` | `prototype/` |

The dev server serves at `http://localhost:5173`.

### Lint caveat

ESLint reports 1 pre-existing warning (`react-hooks/exhaustive-deps` in `ksetra-sevakah.jsx`). This is in the original prototype code and is expected.

### No external services required

The prototype is fully self-contained (static mock data, no backend, no database). No Docker, no API keys, no secrets needed.
