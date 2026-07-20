#!/usr/bin/env bash
# Inicializa el nodo manager del clúster Docker Swarm.
# Ejecutar en la máquina que actuará como manager.
set -euo pipefail

IP_ADDR=${1:-$(hostname -I | awk '{print $1}')}

echo ">> Inicializando Swarm en el manager con IP $IP_ADDR"
docker swarm init --advertise-addr "$IP_ADDR"

echo
echo ">> Token para unir WORKERS (guárdalo, se usa en swarm-join-worker.sh):"
docker swarm join-token worker -q

echo
echo ">> Token para unir otros MANAGERS:"
docker swarm join-token manager -q
