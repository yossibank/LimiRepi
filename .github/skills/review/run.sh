#!/bin/bash

TARGET_BRANCH=${1:-main}

if [ ! -d ".git" ]; then
    echo "Error: This script must be run in a Git repository."
    exit 1
fi


if ! git show-ref --verify --quiet "refs/heads/$TARGET_BRANCH"; then
    if git show-ref --verify --quiet "refs/remotes/origin/$TARGET_BRANCH"; then
        TARGET_BRANCH="origin/$TARGET_BRANCH"
    else
        echo "Error: Branch '$TARGET_BRANCH' not found."
        echo "Usage: review <branch_name>"
        exit 1
    fi
fi

DIFF_OUTPUT=$(git diff "$TARGET_BRANCH...HEAD")

if [ -z "$DIFF_OUTPUT" ]; then
    echo "✅ No changes compared to '$TARGET_BRANCH'."
    exit 0
fi
