#!/usr/bin/env bash
# Publica el stack de la Plataforma de Cursos en el clúster Swarm.
# Ejecutar en el nodo manager, con las imágenes ya publicadas
# en el registro (ver .github/workflows/ci-cd.yml).
set -euo pipefail

export IMAGE_PREFIX=${IMAGE_PREFIX:-ghcr.io/tu-usuario/plataformacurso}
export IMAGE_TAG=${IMAGE_TAG:-latest}
export AWS_REGION=${AWS_REGION:-us-east-1}
export SQS_QUEUE_URL=${SQS_QUEUE_URL:-}

echo ">> Desplegando stack 'plataforma' con imágenes $IMAGE_PREFIX/*:$IMAGE_TAG"
docker stack deploy -c docker-stack.yml plataforma

echo
echo ">> Servicios del stack:"
docker stack services plataforma
