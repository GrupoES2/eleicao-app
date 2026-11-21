#!/usr/bin/env bash
# Identidade de pre-release: proximo patch apos a ultima tag, mais a distancia do commit.
set -euo pipefail

last=""
while IFS= read -r tag; do
  last="$tag"
  break
done < <(git tag -l 'v[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname)

if [[ -z "$last" ]]; then
  base="0.1.0"
  distance="$(git rev-list --count HEAD)"
else
  ver="${last#v}"
  IFS=. read -r major minor patch <<<"$ver"
  patch="$((patch + 1))"
  base="${major}.${minor}.${patch}"
  distance="$(git rev-list --count "${last}..HEAD")"
fi

printf '%s-dev.%s\n' "$base" "$distance"
