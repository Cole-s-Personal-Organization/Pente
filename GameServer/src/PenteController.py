"""
An object which allows management
"""

from typing import List

from GameLobby import Lobby
from PenteModel import PenteGameModel, PenteTurnBuilder

class PenteController():
    def __init__(self, model: PenteGameModel, lobby: Lobby) -> None:

        self.num_in_a_row_to_win = 5
        self.captures_to_win = 5
        self.player_captures = [0] * num_players

        self.game_state = model
        self.game_lobby = lobby

        self.turn_counter = 0

        # mapping of turn
        self.turn_order = []

        self.turn_log: List[PenteTurnBuilder] = []

    def is_won(self, turn: PenteTurnBuilder) -> bool:
        self.check_consecutive_in_row_win_con(turn)
        self.check_capture_win_con()

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
    