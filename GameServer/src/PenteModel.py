from enum import Enum

class board_enums(Enum):
    """words"""
    none = 0
    player_one = 1
    player_two = 2

class PenteTurnBuilder():
    def __init__(self, pos_x: int, pos_y: int, player_number: int) -> None:
        self.pos_x = pos_x
        self.pos_y = pos_y
        self.player_number = player_number
        self.set_is_turn_one_action = False

    def set_is_turn_one_action(self, val: bool)
        self.set_is_turn_one_action = val

    def __str__(self) -> str: #TODO create this method so that turns can be logged
        pass

class PenteGameModel():
    def __init__(self, num_players: int) -> None:
        cols = 19
        rows = 19
        self.game_state = [[board_enums.none for _ in range(cols)] for _ in range(rows)]

    def set_move(self, turn: PenteTurnBuilder): #TODO change the game state to reflect the move, or throw error if invalid move, update the board, check for 5 in a row
        self.check_move(turn)
        #place piece down
        self.check_capture(turn)

    def check_move(self, turn: PenteTurnBuilder)
        """this function checks if a player is placing on top of another player or making a valid turn-one move, or out of bounds"""
        #throw value error
        pass

    def get_board(self)
        return self.game_state