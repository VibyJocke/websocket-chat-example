# websocket-chat-example

## Intro
A simple example how to set up web sockets in a Dropwizard application, and create a simple chat service

## Requirements
- Java 8
- Maven 3

## How to build
Simply run 'mvn package' from root.

## API
Create room
```json
{
    "type": "CREATE-ROOM",
    "content": {
        "name": "",
        "password": ""
    }
}
```json
Join room
```
{
    "type": "JOIN-ROOM",
    "content": {
        "roomName": "",
        "userName": "",
        "password": ""
    }
}
```
Message room
```json
{
    "type": "MESSAGE",
    "content": {
        "roomName": "",
        "message": ""
    }
}
```

## TODO
- Return last X messages sent in room when a client connects.