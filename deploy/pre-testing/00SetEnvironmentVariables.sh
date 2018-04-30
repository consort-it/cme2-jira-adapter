#!/bin/bash
#$1 ENV_PATH .env relative path for docker
#$2 DEPLOYMENT_TARGET like 'dev' or 'live' | can be used to identify scopes or namespaces
#$3 MICROSERVICE_NAME name of microservice
ENV="${1?Need to set ENV_PATH}"
TARGET="${2?Need to set DEPLOYMENT_TARGET}"
NAME="${3?Need to set MICROSERVICE_NAME}"

NAMESPACE="default"
[ "$TARGET" == "dev" ] && NAMESPACE="${CON_IT_KUBERNETES_DEV_NAMESPACE?Need to set env CON_IT_KUBERNETES_DEV_NAMESPACE}"
[ "$TARGET" == "live" ] && NAMESPACE="${CON_IT_KUBERNETES_LIVE_NAMESPACE?Need to set env CON_IT_KUBERNETES_LIVE_NAMESPACE}"
: "${NAMESPACE?Need to set CON_IT_KUBERNETES_XXX_NAMESPACE according DEPLOYMENT_TARGET (2nd Argument)}"

cp ${ENV} "${NAME}/src/test/resources"
