# codacy-roslyn

A standalone tool that converts [Unity Roslyn Analyzers](https://github.com/microsoft/Microsoft.Unity.Analyzers)
diagnostics to Codacy's format.

It allows running Unity Roslyn Analyzers either locally or as part of your CI process and then integrating the results into your Codacy workflow. This way, Codacy will present the results coming from Unity Roslyn Analyzers alongside all the other code quality information in the dashboards.

## Usage

### Requirements

To get your Unity Roslyn Analyzers results into Codacy you'll need to:

-   [Enable Unity Roslyn Analyzers](https://docs.codacy.com/repositories-configure/configuring-code-patterns/) and configure the corresponding code patterns on your repository **Code patterns** page
-   Enable the setting **Run analysis through build server** on your repository **Settings**, tab **General**, **Repository analysis**
-   Obtain a [Project API token](https://docs.codacy.com/codacy-api/api-tokens/#project-api-tokens)
-   Download [codacy-roslyn](https://github.com/codacy/codacy-roslyn/releases)

### Sending the results to Codacy

Sending the results of running Unity Roslyn Analyzers to Codacy involves the steps below, which you can automate in your CI build process:

1.  Run Unity Roslyn Analyzers using the json formatter
2.  Convert the Unity Roslyn Analyzers output to a format that the Codacy API accepts
3.  Send the results to Codacy
4.  Finally, signal that Codacy can use the sent results and start a new analysis

> When the option **“Run analysis through build server”** is enabled, the Codacy analysis will not start until you call the endpoint `/2.0/commit/{commitUuid}/resultsFinal` signalling that Codacy can use the sent results and start a new analysis.

With script:

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"
export CODACY_URL="CODACY-INSTALLATION-URL" # if not defined https://api.codacy.com will be used
export CODACY_ROSLYN_VERSION=0.2.3 # if not defined, latest will be used

# Run the Unity Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
./<codacy-roslyn-path>/scripts/send-results.sh # requires a codacy-roslyn-"<version>" in the current directory
```

Without script (step-by-step):

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"

# 1. Run the Unity Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
# 2. Convert the Unity Roslyn Analyzers output to a format that the Codacy API accepts
./codacy-roslyn-"<version>" | \
# 3. Send the results to Codacy
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" -d @- \
    "https://api.codacy.com/2.0/commit/$COMMIT/issuesRemoteResults"

# 4. Signal that Codacy can use the sent results and start a new analysis
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" \
    "https://api.codacy.com/2.0/commit/$COMMIT/resultsFinal"
```

For self-hosted installations:

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"
export CODACY_URL="CODACY-INSTALLATION-URL"

# 1. Run the Unity Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
# 2. Convert the Unity Roslyn Analyzers output to a format that the Codacy API accepts
./codacy-roslyn-"<version>" | \
# 3. Send the results to Codacy
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" -d @- \
    "$CODACY_URL/2.0/commit/$COMMIT/issuesRemoteResults"

# 4. Signal that Codacy can use the sent results and start a new analysis
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" \
    "$CODACY_URL/2.0/commit/$COMMIT/resultsFinal"
```

* * *

## Building

##### Compile

`sbt compile`

##### Format

`sbt scalafmtAll`

##### Tests

`sbt test`

##### Build native image (requires docker)

`sbt nativeImage`

##### Build fat-jar

`sbt assembly`

##### Generate Docs

```sh
sbt "doc-generator/run"
```

## Agent Playbook: Updating This Repository End-to-End

This section is written for an AI coding agent (or a human) tasked with updating this repo — most commonly bumping the wrapped Unity Roslyn Analyzers version, but also base image / orb / dependency bumps. Follow it top to bottom; it tells you what to change, how to regenerate derived files, how to test locally, and how to interpret CI so you can iterate on failures without guessing.

### 1. What this repository is

This is **not a runnable Codacy engine** in the usual sense: `entry.sh` (the Docker `ENTRYPOINT`) simply prints `roslyn cannot be run by Codacy` and exits 1 — the Docker image only exists to ship `docs/` (patterns metadata) into Codacy's platform. The actual work happens in a separate standalone Scala CLI binary (`src/main/scala/com/codacy/rolsyn/{Main,Converter,Prefixer,RoslynReportParser}.scala`, built with `sbt nativeImage`/`sbt assembly`) that customers download from GitHub Releases and run themselves in their own CI to convert [Unity Roslyn Analyzers](https://github.com/microsoft/Microsoft.Unity.Analyzers) (`dotnet format analyzers` JSON output) into Codacy's results format, then POST the results to the Codacy API via `scripts/send-results.sh`.

`docs/` is machine-consumed configuration, not just documentation:

- `docs/patterns.json` — the full list of Unity Roslyn Analyzers rules ("patterns") Codacy knows about (`patternId`, `level`, `category`) plus the wrapped tool `version`. Generated file, do not hand-edit.
- `docs/description/description.json` + `docs/description/UNT*.md` — human-readable titles/descriptions per pattern, used in the Codacy UI. Generated file, do not hand-edit.
- `docs/tool-description.md` — short blurb about the tool, hand-maintained.

Both generated JSON artifacts and the per-rule `.md` files come from **`DocGenerator`** (`doc-generator/src/main/scala/com/codacy/roslyn/DocGenerator.scala`, run via `sbt "doc-generator/run"`), which `curl`s the real `microsoft/Microsoft.Unity.Analyzers` GitHub repo at ref `$roslynVersion` (its `doc/index.md` table and each rule's `doc/<ruleId>.md`) and rewrites `docs/patterns.json` and `docs/description/*`. This means the generator needs **network access** and **curl** available locally, and requires that a matching tag/ref actually exists upstream at `microsoft/Microsoft.Unity.Analyzers`.

### 2. Files that encode versions — check all of these on every update

| File | What it controls | What to check |
|---|---|---|
| `build.sbt` → `val roslynVersion` | Which Unity Roslyn Analyzers ref the doc generator scrapes | Bump to the target version/tag, confirm a matching ref exists in `microsoft/Microsoft.Unity.Analyzers`. |
| `build.sbt` → `ThisBuild / scalaVersion` and the various library dependencies (`codacy-engine-scala-seed`, `ujson`, `codacy-analysis-cli-model`, `scalatest`, `scala-xml`, `better-files`) | Scala toolchain/runtime deps | Bump opportunistically alongside a version bump (see commit `b7317f1` for a real example touching all of these at once). |
| `project/build.properties` → `sbt.version` | sbt itself | Check the latest supported sbt release. |
| `project/plugins.sbt` → `codacy-sbt-plugin`, `sbt-native-image`, `sbt-assembly` | Build plugins | Check the latest published versions. |
| `.circleci/config.yml` → `codacy/base` orb | Shared CircleCI steps (checkout/version, sbt, shell, publish) | Check the latest published version. |
| `.circleci/config.yml` → `codacy/plugins-test` orb | Would run `codacy-plugins-test` in CI (currently commented out in the workflow) | Only relevant if that job is re-enabled. |
| `Dockerfile` → `FROM alpine:...` | Base image for the (non-functional) Docker image that carries `docs/` | Only bump if there's a reason to (e.g. security/EOL); it has no effect on the actual analyzer conversion logic. |
| `.version` | Repo version file consumed by CI (`codacy/checkout_and_version` with `write_sbt_version: true`) | Not hand-edited for a tool bump — it's written by the CircleCI orb job during the build, based on the git history/tags. |

### 3. Step-by-step update procedure

1. **Bump `roslynVersion` in `build.sbt`** (and any dependency versions you're also updating) as scoped by the task.
2. **Regenerate the docs**: `sbt "doc-generator/run"`. Review the diff to `docs/patterns.json` and `docs/description/*` for new/removed/renamed rules (`UNT####`), and update the `Converter`/`RoslynReportParser` tests under `src/test` if rule shapes changed.
3. **Compile, format, and test**: `sbt scalafmtAll`, `sbt compile`, `sbt test`.
4. **Build the native image and/or fat-jar** to confirm packaging still works: `sbt nativeImage` (requires Docker) and `sbt assembly`.
5. **Build the Docker image** (`docker build -t codacy-roslyn .`) to confirm it still builds, keeping in mind it only ships `docs/` and `entry.sh` — it is not expected to actually run analysis.
6. **`codacy-plugins-test` is not currently wired into this repo's CI** (the `plugins_test` job in `.circleci/config.yml` is commented out) — there is no local plugins-test validation step to run here. Rely on `sbt test` (`ConverterSpecs`, `RoslynReportParserSpecs`) as the real regression suite, and manually sanity-check the converter against `src/test/resources/report.json`-style input if you changed parsing logic.
7. **Iterate on failures**, re-running only the relevant command after each fix.
8. **Commit** the version bump(s) together with the regenerated `docs/` files in one change.
9. **Push and open a PR.**
10. **Poll the PR's real CI checks until they all pass — local validation is NOT the finish line.** After every push, run `gh pr checks <pr-url>` and keep re-polling (short sleep while any check is `pending`) until all checks finish. If a check fails, fetch its actual log (don't guess), find the true root cause, fix it, push again (never `--no-verify`, never force-push), and re-poll. Repeat until every check is green. **The CI environment's toolchain can differ from your local one**, so a clean local run does not guarantee CI passes. Only stop iterating when every check passes, or you hit a genuine product/infra decision that needs a human.

### 4. Common failure modes and fixes

| Symptom | Likely cause | Fix |
|---|---|---|
| `DocGenerator` run fails to fetch `doc/index.md` or a rule's `.md` file | The target `roslynVersion` string doesn't match an existing tag/ref in `microsoft/Microsoft.Unity.Analyzers`, or the upstream `doc/` layout changed | Verify the exact ref name upstream before bumping `build.sbt`; adjust `DocGenerator`'s parsing (`buildUrl`, `parseRulesLine`, category-to-level mapping in `toCodacyCategory`/`toCodacyLevel`) if the table format changed. |
| `toCodacyCategory` throws a `MatchError` after regenerating docs | Upstream added a new rule category not in the `Correctness` / `Performance` / `Type Safety` match | Add the new category mapping in `DocGenerator.toCodacyCategory`. |

### 5. Definition of done

- `roslynVersion` (and any other bumped dependency versions) reflected in `build.sbt`, `project/build.properties`, `project/plugins.sbt`, and `.circleci/config.yml` as applicable.
- `docs/patterns.json` and `docs/description/*` regenerated via `sbt "doc-generator/run"` and committed, with any new/removed rules accounted for.
- `sbt scalafmtAll`, `sbt compile`, and `sbt test` pass locally.
- `sbt nativeImage` / `sbt assembly` and `docker build` succeed.
- **After pushing and opening/updating the PR, every CI check on it is green.** Poll `gh pr checks <pr-url>` and iterate on any failure until all pass.

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

-   Identify new Static Analysis issues
-   Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
-   Auto-comments on Commits and Pull Requests
-   Integrations with Slack, HipChat, Jira, YouTrack
-   Track issues Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.
