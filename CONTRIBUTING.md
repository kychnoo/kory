# Contributing to Kory

Thanks for your interest in contributing! This guide covers the process for getting started.

## Prerequisites

- **JDK 21** — the project uses Java 21 toolchain.
- **IntelliJ IDEA 2026.1.1** or later.
- **Git**

## Getting Started

### 1. Fork and Clone

```bash
# Fork on GitHub: https://github.com/kychnoo/kory

# Clone your fork
git clone https://github.com/kychnoo/kory.git
cd kory
```

### 2. Open in IntelliJ IDEA

Open the project root directory in IntelliJ IDEA. The IDE will automatically detect the Gradle project and import all modules.

### 3. Build

```bash
./gradlew build
```

### 4. Run Tests

```bash
./gradlew test
```

## Project Structure

```
kory/
├── core/                 # Provider-agnostic abstractions
├── openai/               # OpenAI-compatible client
├── kory-ktor/            # HTTP transport layer
├── kory-ktor-cio/        # CIO engine for kory-ktor
├── app/                  # Example application
├── buildSrc/             # Convention plugins
├── examples/             # Code examples for documentation
└── gradle/               # Version catalog
```

## Making Changes

### Branching

- `master` is the main branch.
- Create a feature branch from `master`:
  ```bash
  git checkout -b feature/my-feature master
  ```

### Code Style

- Follow existing code conventions in each module.
- Use `internal` for non-public APIs.
- All public APIs must have KDoc with `@param`, `@return`, `@throws`, `@property`, and `@sample` tags where applicable.
- Do not add dependencies unless necessary and discussed in an issue first.

### Tests

- Add unit tests for new functionality.
- Tests use JUnit 5 via `kotlin("test")` — no additional test frameworks.
- Follow the existing test naming convention: `<ClassName>Test`.
- Run `./gradlew test` before submitting.

### Commit Messages

- Use clear, descriptive commit messages.
- Reference issues when applicable: `fix: resolve tool call parsing (#42)`.

## Pull Requests

1. Ensure your branch is up to date with `master`:
   ```bash
   git fetch origin
   git rebase origin/master
   ```
2. Push your branch and open a PR against `master`.
3. Fill in the PR description:
   - What changed and why.
   - How to test the changes.
   - Any breaking changes.
4. Link the related issue (if any).
5. Wait for CI to pass and for a review.

### PR Checklist

- [ ] Code compiles without errors (`./gradlew build`).
- [ ] All tests pass (`./gradlew test`).
- [ ] New public APIs have KDoc.
- [ ] No unnecessary dependencies added.
- [ ] Code follows existing conventions.

## Reporting Issues

Open an issue on GitHub with:
- A clear title and description.
- Steps to reproduce (if applicable).
- Expected vs actual behavior.

## Questions?

Open a discussion on GitHub if you have questions before starting work on a change.
