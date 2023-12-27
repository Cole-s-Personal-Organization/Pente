
use super::pente_turn_builder::PenteTurnBuilder;

pub struct PenteGameBoard {
    board: Vec<Vec<u8>>
}

impl PenteGameBoard {
    pub fn new() -> Self {
        let rows = 19;
        let cols = 19;
        PenteGameBoard {
            board: vec![vec![0; cols]; rows]
        }
    }

    pub fn make_player_move(self, turn: &PenteTurnBuilder) {
        if self.is_next_move_valid(turn) {
            // place the 
        }
    }

    // validate that the next move is correct
    // this assumes that every previous move has been valid
    fn is_next_move_valid(self, turn: &PenteTurnBuilder) -> bool {
        // TODO
        true
    }

    fn validate_curr_board_state(self) -> bool {
        // TODO: this function may be unecessary or nice to have
        true
    }

    fn get_board(self) -> Vec<Vec<u8>> {
        self.board
    }
}