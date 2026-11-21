#!/usr/bin/env bash
# Escreve no stdout, um por linha, quem aprovou um pull request associado ao SHA.
set -euo pipefail

sha="${1:?sha}"
repo="${GITHUB_REPOSITORY:?}"

numbers="$(gh api "repos/${repo}/commits/${sha}/pulls" --jq '.[].number')"
found=""

for number in $numbers; do
  logins="$(gh api "repos/${repo}/pulls/${number}/reviews" --jq '.[] | select(.state=="APPROVED") | .user.login')"
  if [[ -n "$logins" ]]; then
    found+="${logins}"$'\n'
  fi
done

if [[ -z "$found" ]]; then
  echo "O SHA ${sha} nao tem pull request aprovado em desenvolvimento." >&2
  exit 1
fi

printf '%s' "$found" | sed '/^$/d' | sort -u
