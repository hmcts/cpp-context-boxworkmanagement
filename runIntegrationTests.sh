#!/usr/bin/env bash

CONTEXT_NAME=boxworkmanagement

FRAMEWORK_LIBRARIES_VERSION=17.101.2
FRAMEWORK_VERSION=17.101.6
EVENT_STORE_VERSION=17.101.5

DOCKER_CONTAINER_REGISTRY_HOST_NAME=crmdvrepo01

[ -z "$CPP_DOCKER_DIR" ] && echo "Please export CPP_DOCKER_DIR environment variable pointing to cpp-developers-docker repo (https://github.com/hmcts/cpp-developers-docker) checked out locally" && exit 1
WILDFLY_DEPLOYMENT_DIR="$CPP_DOCKER_DIR/containers/wildfly/deployments"

source $CPP_DOCKER_DIR/docker-utility-functions.sh
source $CPP_DOCKER_DIR/build-scripts/integration-test-scipt-functions.sh

buildDeployAndTest() {
  loginToDockerContainerRegistry
  buildWars
  undeployWarsFromDocker
  buildAndStartContainers
  deployCamundaRestEngine
  deployWars
  deployWiremock
  healthchecks
  integrationTests
}

buildDeployAndTest
