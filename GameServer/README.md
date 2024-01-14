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
This project will be utilizing a maven build system, standard maven commands will be applicable here.

- Build Commands
    - Build the project:

            mvn package

- Run Commands
    - Run the JAR file:

            java -cp target/GameServer-1.0-SNAPSHOT.jar com.mycompany.app.App

- Run Tests
    
        mvn test

