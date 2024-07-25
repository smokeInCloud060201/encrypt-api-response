ifndef DOCKER_BIN:
	DOCKER_BIN = docker
endif

ifndef DOCKER_COMPOSE_BIN:
	DOCKER_COMPOSE_BIN = docker-compose
endif

ifndef CLIENT_SERVICE_PATH:
	CLIENT_SERVICE_PATH = ./client-service
endif

ifndef RESOURCE_SERVICE_PATH:
	RESOURCE_SERVICE_PATH = ./resource-service
endif

ifndef CLIENT_IMAGE:
	CLIENT_IMAGE = client-service
endif

ifndef RESOURCE_IMAGE:
	RESOURCE_IMAGE = resource-service
endif

init-image: build-client-image build-resource-image

build-client-image: wrapper-maven-client
build-client-image:
	${DOCKER_BIN} build -t ${CLIENT_IMAGE}:latest -f ${CLIENT_SERVICE_PATH}/Dockerfile ${CLIENT_SERVICE_PATH}

build-resource-image: wrapper-maven-resource
build-resource-image:
	${DOCKER_BIN} build -t ${RESOURCE_IMAGE}:latest -f ${RESOURCE_SERVICE_PATH}/Dockerfile ${RESOURCE_SERVICE_PATH}

wrapper-maven-client:
	mvn -f ${CLIENT_SERVICE_PATH} wrapper:wrapper

wrapper-maven-resource:
	mvn -f ${RESOURCE_SERVICE_PATH} wrapper:wrapper

