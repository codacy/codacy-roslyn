# codacy-staticcheck

A standalone tool that converts [staticcheck](https://staticcheck.io/)
diagnostics to Codacy's format.

It allows running staticcheck either locally or as part of your CI process and then integrating the results into your Codacy workflow. This way, Codacy will present the results coming from staticcheck alongside all the other code quality information in the dashboards.

## Usage

### Requirements

To get your staticcheck results into Codacy you'll need to:

-   Enable the setting “Run analysis through build server” under your repository Settings > General > Repository analysis
-   Obtain a [Project API token](https://support.codacy.com/hc/en-us/articles/207994675-Project-API)
-   Download [codacy-staticcheck](https://github.com/codacy/codacy-staticcheck/releases)

### Sending the results to Codacy

Sending the results of running staticcheck to Codacy involves the steps below, which you can automate in your CI build process:

1.  Run staticcheck using the json formatter
2.  Convert the staticcheck output to a format that the Codacy API accepts
3.  Send the results to Codacy
4.  Finally, signal that Codacy can use the sent results and start a new analysis

With script:

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"
export CODACY_URL="CODACY-INSTALLATION-URL" # if not defined https://api.codacy.com will be used
export CODACY_CLANG_TIDY_VERSION=0.2.3 # if not defined, latest will be used

staticcheck -f json "<staticcheck-configs>" | \
./<codacy-staticcheck-path>/scripts/send-results.sh # requires a codacy-staticcheck-"<version>" in the current directory
```

Without script (step-by-step):

```bash
export PROJECT_TOKEN="YOUR-TOKEN"
export COMMIT="COMMIT-UUID"

# 1. Run staticcheck
staticcheck -f json "<staticcheck-configs>" | \
# 2. Convert the staticcheck output to a format that the Codacy API accepts
./codacy-staticcheck-"<version>" | \
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

# 1. Run staticcheck
staticcheck -f json "<staticcheck-configs>" | \
# 2. Convert the staticcheck output to a format that the Codacy API accepts
./codacy-staticcheck-"<version>" | \
# 3. Send the results to Codacy
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" -d @- \
    "$CODACY_URL/2.0/commit/$COMMIT/issuesRemoteResults"

# 4. Signal that Codacy can use the sent results and start a new analysis
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
	-H "Content-type: application/json" \
	"$CODACY_URL/2.0/commit/$COMMIT/resultsFinal"
```

> When the option **“Run analysis through build server”** is enabled, the Codacy analysis will not start until you call the endpoint `/2.0/commit/{commitUuid}/resultsFinal` signalling that Codacy can use the sent results and start a new analysis.

* * *

## Building

##### Compile

`sbt compile`

##### Format

`sbt scalafmt test:scalafmt sbt:scalafmt`

##### Tests

`sbt test`

##### Build native image (requires docker)

`sbt "graalvm-native-image:packageBin"`

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
