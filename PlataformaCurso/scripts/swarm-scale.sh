#!/usr/bin/env bash
# Escala un servicio del stack hacia arriba o hacia abajo.
# Uso: ./swarm-scale.sh apigateway 4
set -euo pipefail

SERVICE=${1:?"Indica el servicio, ej: apigateway"}
REPLICAS=${2:?"Indica el número de réplicas, ej: 4"}

echo ">> Escalando plataforma_${SERVICE} a ${REPLICAS} réplicas"
docker service scale "plataforma_${SERVICE}=${REPLICAS}"

echo
echo ">> Estado actual:"
docker service ps "plataforma_${SERVICE}"
