# codacy-roslyn

A standalone tool that converts [Roslyn Analyzers](https://github.com/dotnet/roslyn-analyzers)
diagnostics to Codacy's format.

It allows running Roslyn Analyzers either locally or as part of your CI process and then integrating the results into your Codacy workflow. This way, Codacy will present the results coming from Roslyn Analyzers alongside all the other code quality information in the dashboards.

## Usage

### Requirements

To get your Roslyn Analyzers results into Codacy you'll need to:

-   [Enable Roslyn Analyzers](https://docs.codacy.com/repositories-configure/configuring-code-patterns/) and configure the corresponding code patterns on your repository **Code patterns** page
-   Enable the setting **Run analysis through build server** on your repository **Settings**, tab **General**, **Repository analysis**
-   Obtain a [Project API token](https://docs.codacy.com/codacy-api/api-tokens/#project-api-tokens)
-   Download [codacy-roslyn](https://github.com/codacy/codacy-roslyn/releases)

### Sending the results to Codacy

Sending the results of running Roslyn Analyzers to Codacy involves the steps below, which you can automate in your CI build process:

1.  Run Roslyn Analyzer using the json formatter
2.  Convert the Roslyn Analyzers output to a format that the Codacy API accepts
3.  Send the results to Codacy
4.  Finally, signal that Codacy can use the sent results and start a new analysis

> When the option **“Run analysis through build server”** is enabled, the Codacy analysis will not start until you call the endpoint `/2.0/commit/{commitUuid}/resultsFinal` signalling that Codacy can use the sent results and start a new analysis.

With script:

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"
export CODACY_URL="CODACY-INSTALLATION-URL" # if not defined https://api.codacy.com will be used
export CODACY_ROSLYN_VERSION=0.2.3 # if not defined, latest will be used

# Run the Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
./<codacy-roslyn-path>/scripts/send-results.sh # requires a codacy-roslyn-"<version>" in the current directory
```

Without script (step-by-step):

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"

# 1. Run the Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
# 2. Convert the Roslyn Analyzers output to a format that the Codacy API accepts
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

# 1. Run the Roslyn Analyzers to generate the report file
dotnet format analyzers --verify-no-changes --report report

cat report/format-report.json | \
# 2. Convert the Roslyn Analyzers output to a format that the Codacy API accepts
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
