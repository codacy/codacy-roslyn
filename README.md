# codacy-roslyn

A standalone tool that converts [roslyn](https://github.com/dotnet/roslyn-analyzers)
diagnostics to Codacy's format.

It allows running roslyn analyzers either locally or as part of your CI process and then integrating the results into your Codacy workflow. This way, Codacy will present the results coming from roslyn analyzers alongside all the other code quality information in the dashboards.

## Usage

### Requirements

To get your roslyn results into Codacy you'll need to:

-   Enable the setting “Run analysis through build server” under your repository Settings > General > Repository analysis
-   Obtain a [Project API token](https://support.codacy.com/hc/en-us/articles/207994675-Project-API)
-   Download [codacy-roslyn](https://github.com/codacy/codacy-roslyn/releases)

### Sending the results to Codacy

Sending the results of running roslyn analyzers to Codacy involves the steps below, which you can automate in your CI build process:

1.  Run roslyn using the json formatter
2.  Convert the roslyn output to a format that the Codacy API accepts
3.  Send the results to Codacy
4.  Finally, signal that Codacy can use the sent results and start a new analysis

> When the option **“Run analysis through build server”** is enabled, the Codacy analysis will not start until you call the endpoint `/2.0/commit/{commitUuid}/resultsFinal` signalling that Codacy can use the sent results and start a new analysis.

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