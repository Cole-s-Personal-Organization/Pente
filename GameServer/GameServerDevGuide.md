# GameServer Development Guide

This guide will get developers familiar with the projects development cycle and other useful information.

# Important Commands
This project's java backend will be packaged into a docker container and served by Apache Tomcat. A Maven build system will be used here. All Maven commands apply within the context of the project. Same applies for docker/docker-compose.

- Build Command
    - Build the project outside dockerfile:

            mvn package
    
    - Build the Dockerfile alone

            docker build -t <image-name> .

- Run Commands
    
    - Run the docker image alone:

            docker run -p 8080:8080 <image-name>

    - Build and run with dependant services:

            docker compose up --build -d gameserver

- Stop Commands

    - Stop lone docker container:

            docker ps

            docker stop <container-id>

    - Stop docker compose

            docker compose down

- Run Tests
    
        mvn test


## How Should You Use These In Development

In most cases, it is better to lead your development by via test driven development (TDD). This means:
1. Stub out (create function headers) for functions you will need.
1. Create failing tests which conceptually describe how your code should act.
1. Develop the internal code until passing.
1. Repeat...

As such, it is best that unless you need to run the full service with docker, you should never actually build and run the project. **Just run tests** with mvn test. This will lead to better, more maintainable code with a quicker feedback loop.