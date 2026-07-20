#!/usr/bin/env bash
# Une este nodo al clúster Swarm como worker.
# Uso: ./swarm-join-worker.sh <TOKEN> <IP_DEL_MANAGER>
set -euo pipefail

TOKEN=${1:?"Falta el token de worker (docker swarm join-token worker -q en el manager)"}
MANAGER_IP=${2:?"Falta la IP del manager, ej: 192.168.1.10:2377"}

echo ">> Uniendo este nodo como worker al manager $MANAGER_IP"
docker swarm join --token "$TOKEN" "$MANAGER_IP"

echo ">> Nodo unido. Verifica desde el manager con: docker node ls"
