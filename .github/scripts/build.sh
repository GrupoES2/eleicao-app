#!/usr/bin/env bash
# Builda os tres artefatos com a versao informada e grava SHA256SUMS.
# O segundo argumento e o diretorio de saida. O terceiro e release ou dev.
set -euo pipefail

version="${1:?versao}"
dest="${2:?destino}"
mode="${3:-release}"

root="$(cd "$(dirname "$0")/../.." && pwd)"
mkdir -p "$dest"
dest="$(cd "$dest" && pwd)"

export TZ=UTC
export SOURCE_DATE_EPOCH=1577836800
export NG_CLI_ANALYTICS=false
stamp="2020-01-01T00:00:00Z"

build_jar() {
  local module="$1"
  local artifact="$2"
  cd "$root/api/$module"
  mvn -B -ntp org.codehaus.mojo:versions-maven-plugin:2.18.0:set \
    -DnewVersion="$version" \
    -DgenerateBackupPoms=false
  mvn -B -ntp -DskipTests package -Dproject.build.outputTimestamp="$stamp"
  cp "target/${artifact}-${version}.jar" "$dest/${artifact}-${version}.jar"
}

if [[ "$mode" == "dev" ]]; then
  ng_config="development"
else
  ng_config="production"
fi

build_jar eleicoes-app eleicoes-app
build_jar votacao votacao

cd "$root/mobile"
npm ci
npx ng build --configuration="$ng_config"
find www -exec touch -t 202001010000.00 {} +
(
  cd www
  find . -type f | LC_ALL=C sort | zip -X -q "$dest/mobile-${version}.zip" -@
)

(
  cd "$dest"
  sha256sum "eleicoes-app-${version}.jar" "votacao-${version}.jar" "mobile-${version}.zip" > SHA256SUMS
)
