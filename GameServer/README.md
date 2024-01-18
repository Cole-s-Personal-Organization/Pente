# GameServer
A webserver which provides an interface for users to play Pente.

# Gameserver Project Structure


        ├── README.md
        ├── docs
        ├── pom.xml
        └── src
            ├── main
            │   └── java
            │       └── com
            │           └── mycompany
            │               └── app
            │                   ├── App.java
            │                   ├── Game
            │                   │   └── Pente
            │                   │       └── (1)*
            │                   └── WebServer
            │                       └── (2)*
            └── test
                └── java
                    └── com
                        └── mycompany
                            └── app
                                └── (3)*

## Notable Subdirectories

1. *src/main/java/com/company/app/Game/Pente/* - Houses all source code which pertains to the underlying logic of Pente.
1. *src/main/java/com/company/app/Webserver/* - The implementation of the webserver which handles socket messages and interprets them into understandlable commands for the games it holds.
1. *src/test/java/com/mycompany/app/* - Houses all testing for the webserver.

# Important Commands
This project's java backend will be packaged into a docker container and served by Apache Tomcat. A Maven build system will be used here, so all standard maven commands can be used here.

- Build Command
    - Build the project outside dockerfile:

            mvn package
    
    - Build the Dockerfile

            docker build -t <image-name> .

- Run Commands
    
    - Run the docker image:

            docker run -p 8080:8080 <image-name>

- Run Tests
    
        mvn test