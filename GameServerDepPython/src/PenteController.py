"""
An object which allows management
"""

from typing import List

from AbstractGameController import AbstractGameController
from GameLobby import Lobby
from PenteModel import PenteGameModel, PenteTurnBuilder

class PenteController(AbstractGameController):
    def __init__(self, model: PenteGameModel, lobby: Lobby) -> None:

        self.game_state = model

        self.turn_counter = 0

        # mapping of turn
        self.turn_order = []

        self.turn_log: List[PenteTurnBuilder] = []

    
    def run(self):
        """
        """
        pass 

        

    def game_loop(): 
        pass

    def await_player_turn():
        pass

    def take_player_turn(self, turn_data):
        player_id = turn_data["player_id"]


        # check if user is allowed to play 


        # using turn builder create a useable turn 

        # use turn to update game state
        
        
        pass

    def notify_player_turn():
        """Notify the player that they need to take their next turn."""
        pass
    