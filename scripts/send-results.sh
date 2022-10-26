#!/usr/bin/env bash

if [ -z "$CODACY_URL" ]
then
    CODACY_URL="https://api.codacy.com"
    echo "\$CODACY_URL is not defined. Using $CODACY_URL as destiny URL"
fi

if [ -z "$PROJECT_TOKEN" ]
then
    echo "\$PROJECT_TOKEN is not defined and it is required to send the results back to Codacy"
    exit 1
fi

if [ -z "$COMMIT" ]
then
    echo "\$COMMIT is not defined and it is required to send the results back to Codacy"
    exit 1
fi

if [ -z "$CODACY_ROSLYN_VERSION" ]
then
    CODACY_ROSLYN_VERSION="latest"
    echo "\$CODACY_ROSLYN_VERSION not defined. Using $CODACY_ROSLYN_VERSION as tool version"
fi

./codacy-roslyn-$CODACY_ROSLYN_VERSION | \
curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
    -H "Content-type: application/json" -d @- \
    "$CODACY_URL/2.0/commit/$COMMIT/issuesRemoteResults"

curl -XPOST -L -H "project-token: $PROJECT_TOKEN" \
	-H "Content-type: application/json" \
	"$CODACY_URL/2.0/commit/$COMMIT/resultsFinal"