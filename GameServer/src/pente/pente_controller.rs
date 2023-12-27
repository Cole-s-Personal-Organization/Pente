use super::pente_game_board_model::PenteGameBoard;


pub struct PenteController<'a> {
    num_in_row_to_win: u8,
    captures_to_win: u8,
    player_captures: Vec<u8>,

    pente_game_board_model: &'a PenteGameBoard
}

impl<'a> PenteController<'a> {
    pub fn new(model: &'a PenteGameBoard, num_players: u8) -> Self {
        PenteController {
            num_in_row_to_win: 5,
            captures_to_win: 5,
            player_captures: vec![0; num_players as usize],
            pente_game_board_model: model
        }
    }
}