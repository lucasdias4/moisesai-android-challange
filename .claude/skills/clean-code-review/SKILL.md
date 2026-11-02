---
name: clean-code-review
description: Review a module, package or diff against Clean Code (Robert C. Martin) and this project's conventions, then fix what the review finds - names, functions, classes and files, variables and nullability, dead code, magic strings, duplication, visibility, comments and tests. Use when asked to review, audit or clean up code, and as the final check of any skill.
---

The rules are in `ARCHITECTURE.md` §8 and the vocabulary in §3. This skill is how to review against them and report.

## When to use

- "Review everything we built", a module audit, or a pre-commit cleanup
- The final "Clean code check" step of another skill

## Steps

### 1. Scope

List the files: `git status --short`, `git diff --stat`, or `find <module>/src -name "*.kt"`. Read every file in scope in full. Read the tests too.

### 2. Run the mechanical checks

- Compiler warnings: `./gradlew clean assembleDebug` and read the `w:` lines (unused variables, parameters, imports).
- Lint: `./gradlew lintDebug`.
- Unused code the compiler cannot see: for each public class, function, property and constructor parameter, search the repository for a use. No use outside tests means delete it (YAGNI), unless a documented next step needs it.
- Stale names after a rename: search for the old names in code, tests, docs and skills (the searcher may skip git-ignored files such as `PLAN.md`; name them explicitly).

### 3. Review against the checklist

| Area | Ask |
|---|---|
| Names | Does the name say what it is for? Nouns for types, verbs for functions, booleans as questions? Vocabulary kept (track/album, `id`/`name`, `albumId`)? Parameters named by meaning, wire names only in annotations? No `Manager`, `Helper`, `Util`, `Handler`, `Data`, `Info`? |
| Functions | Small, one thing, one level of abstraction? At most three parameters? No flag or nullable-as-default parameter? Command or query, not both? Error handling separate from logic? |
| Classes and files | One class per file? One responsibility, high cohesion? Packages by kind (`api`, `model`, `extension`, `datasource`, `di`)? Extension files named `...Extensions.kt` or `...Extension.kt`? |
| Visibility | `internal` or `private` unless another module needs it? |
| Variables | `val` over `var`? Declared near use? No magic numbers or strings (fixed query parameters in the `@GET`)? |
| Nullability | Each nullable field backed by real data or the docs? No `= null` defaults on DTOs? |
| Comments | Only *why*? No commented-out code, no `TODO` left, no noise, no comment that duplicates the name? (Animation files are the exception: every constant, animated value and non-obvious call has a short comment on its meaning and reason.) |
| Animations | In an `animation` package next to the screen, one per file, name and file end in `Animation`? One `Phase` timeline? Animated state read in draw or `graphicsLayer`? See `create-animation`. |
| Components | Name and file end in `Component`, in the `component` package, with `@FullPreview` previews in the same file? Screen, layout, icon, modifier and preview files in their own packages? |
| Duplication | Anything written three times extracted? |
| Dead code | Unused fields, error types, dispatchers, dependencies, imports, files? |
| Layering | Datasource raw and interface-free, no dispatcher; repository maps; ViewModel calls use cases only; no feature imports another? |
| Tests | One concept each, sentence names, fixtures real, shared support, no logic, none passing with the code broken? |

### 4. Decide what to ask

Fix what is clearly a violation. **Ask the user** about a naming or structure choice that is a matter of taste, or where two rules conflict, and say why. Leave template or placeholder code that the plan replaces soon, and say so.

### 5. Fix and re-verify

Apply the fixes, then re-run `./gradlew clean assembleDebug testDebugUnitTest lintDebug`. After a rename, search the code, tests, docs and skills for the old names.

### 6. Report

- **Findings:** file, what, why it breaks which rule
- **Fixed** and **left as is** (with the reason)
- **Questions** for the user
- The build and test result
