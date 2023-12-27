
from typing import List
from flask import Flask, request
from flask_socketio import SocketIO, Namespace

class Player():
    def __init__(self, player_ip: str, player_name: str) -> None:
        self.player_ip = player_ip
        self.player_name = player_name


class Lobby():
    """
    A class for holding players and a game session.
    """
    def __init__(self, socketio: SocketIO, lobby_creator: Player, lobby_name: str, selected_game: str, max_num_players: int = 4) -> None:
        self.lobby_name = lobby_name
        self.selected_game: str = selected_game # game selected for lobby will remain static for implemenation simplicity
        self.namespace_id = "lobby_" + lobby_name 
        self.max_num_players = max_num_players
        self.socketio = socketio

        self.lobby_host_player: Player = lobby_creator
        self.players: List[Player] = []

        # connect handlers to events
        self.socketio.on_event('connect', self.on_connect_handler, namespace=self.lobby_name)
        self.socketio.on_event('disconnect', self.on_disconnect_handler, namespace=self.lobby_name)

    def remove_player(self, player_ip: str, player_name: str) -> None:
        """Removes first instance of Player object with both player_ip and player_name"""
        for i in range(len(self.players)):
            player = self.players[i]
            if player.player_ip == player_ip and player.player_name == player_name:
                self.players.pop(i)
                return
    
    def is_player_connected(self, player_ip: str, player_name: str) -> bool:
        """Checks to see if unique combo of ip and selected name is currently in use in lobby"""
        for i in range(len(self.players)):
            player = self.players[i]
            if player.player_ip == player_ip and player.player_name == player_name:
                return True
        return False
    
    def is_player_host(self, player: Player) -> bool:
        """Checks if the player passed in is the lobby's host"""
        if player.player_ip == self.lobby_host_player.player_ip and player.player_name == self.lobby_host_player.player_name:
            return True
        else: 
            return False

    def get_num_players(self) -> int:
        return len(self.players)
    
    
    def send_message_to_group(self, message: str):
        self.socketio.send(message)

    def on_connect_handler(self, data):
        client_sid = request.sid
        try:
            player_name = data["player_name"] 
            player_ip = data["player_ip"]

            if self.get_num_players() + 1 >= self.max_num_players:
                raise ConnectionRefusedError("Player Limit already reached, cannot join")

            if self.is_player_connected(player_name, player_ip): # prevent duplicate players
                raise ConnectionRefusedError("Player already exists, connection blocked")
            

            self.players.append(Player(player_ip, player_name))
            self.socketio.emit("player_connected", {"message": f"Player Connected: {player_name}"}, room=client_sid)
        except Exception as e:
            print(f"Error connecting player: {e}")
            # Send an error message to the connecting client
            self.socketio.emit("connection_error", {"error": "Failed to connect player"}, room=client_sid)
            

    def on_disconnect_handler(self, data): 
        client_sid = request.sid
        try:
            player_name = data["player_name"] 
            player_ip = data["player_ip"]

            self.remove_player(player_ip, player_name)

            if self.is_player_host(Player(player_ip, player_name)):
                # if disconnecting player is host, promote a new player
                if self.get_num_players() > 0:
                    self.lobby_host_player = self.players[0]
            
            self.socketio.emit("player_connected", {"message": f"Player Connected: {player_name}"}, room=client_sid)
        except Exception as e:
            print(f"Error connecting player: {e}")
            # Send an error message to the connecting client
            self.socketio.emit("connection_error", {"error": "Failed to connect player"}, room=client_sid)


class GamePool():
    """A class for holding the complete list of availible games."""
    def __init__(self, socketio: SocketIO) -> None:
        self.socketio = socketio

        self.lobbies: List[Lobby] = []

        # connect handlers to events
        self.socketio.on_event('create_lobby', self.create_lobby)
        self.socketio.on_event('destroy_lobby', self.destroy_lobby)


    def get_num_lobbies(self) -> int:
        return len(self.lobbies)
    
    def get_lobby_headers(self) -> List[dict]:
        """Get all basic headline information about current running lobby"""
        game_header_list = []
        for lobby in self.lobbies:
            game_header_list.append({
                "lobby_name": lobby.lobby_name,
                "selected_game": lobby.selected_game,
                "host_name": lobby.lobby_host_player.player_name,
                "num_players": lobby.get_num_players()
            })
        return game_header_list



    def destroy_lobby(self, lobby: Lobby):
        lobby.send_message_to_group("Lobby " + lobby.lobby_name + " shutting down")

        self.lobbies.remove(lobby)
    
    def create_lobby(self, new_lobby: Lobby):
        self.lobbies.append(new_lobby)


