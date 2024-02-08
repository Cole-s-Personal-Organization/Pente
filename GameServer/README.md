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
1. *src/main/java/com/company/app/Webserver/* - The implementation of the webserver which handles socket messages and interprets them into understandable commands for the games it holds.
1. *src/test/java/com/mycompany/app/* - Houses all testing for the webserver.


# Data Structures and Data Storage

## Redis
In order to provide realtime speeds to the gameserver, we will be using redis to cache data that is in use by our gameserver service. 

### For Pente 
To fit the key value structure of redis our game data will need to be restructured to fit in a reasonable way to take advantage of redis. This will entail changing the following json representation of a game:

**NOTE:** the following data was randomly created, it is just an example of what the shape of the data is like.

    {
        gameId: "5d976850-2368-4f81-b1aa-ed60fb4d5366",
        lobbyName: "Cole's Game",
        gameCreator: "fc325856-7e54-4d60-92ff-1764a6b9f9f5"
        timeCreatedAt: "2024/03/31 07:02:10"
        gameRunningState: "Created" | "Running" | "Ended"

    
        players: [
            "fc325856-7e54-4d60-92ff-1764a6b9f9f5",
            "22a33f22-292c-406f-b819-c1e13eea9ccc"
        ]

        playerCaptures: [
            0,
            1
        ]
        
        gameSettings: {
            numInARowToWin: 5,
            capturesToWin: 5
        },

        boardState: [
            [0, ..., 1], 
            ..., 
            [0, ..., 2]
        ],

        playLog: [
            { playerNum: 1, timePlayed: "2024/05/18 04:55:00", posX: 1, posY: 4 },
            { playerNum: 2, timePlayed: "2024/06/11 16:25:32", posX: 3, posY: 7 },
        ]
    }

Will be converted into:

1. HSET for game header
        
        penteGame:<gameId> header '{"lobbyName": <lobbyName>, ...}'

1. SET for players

        penteGame:<gameId>:players <playerId>

1. String value for captures

        penteGame:<gameId>:playerCaptures "[0, 1]"

1. HSET for settings

        penteGame:<gameId> settings '{"numInARowToWin": 5, "capturesToWin": 5}'

1. String value for current board state

        penteGame:<gameId>:board "[[], ..., []]"

1. LIST for playLog, *ordered by insertion order*

        penteGame:<gameId>:playLog '{"playerNum": 1, ...}'

## MySQL

Our project requires a long term database solution to be used for storing game logs for future projects and improvement of the gameserver service. 