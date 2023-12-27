

pub struct PenteTurn {
    pos_x: u8, 
    pos_y: u8,
    player_num: u8,
    turn_num: Option<u8> // not needed for majority of cases, unless ruleset dictates a turn num is special 
}

// struct to construct a PenteTurn
pub struct PenteTurnBuilder {
    pub pos_x: u8, 
    pub pos_y: u8,
    pub player_num: u8,
    pub turn_num: Option<u8> 
}

impl PenteTurnBuilder {
    fn new(pos_x: u8, pos_y: u8, player_num: u8) -> Self {
        PenteTurnBuilder { 
            pos_x: pos_x, 
            pos_y: pos_y, 
            player_num: player_num, 
            turn_num: None }
    } 

    fn build(self) -> PenteTurn {
        PenteTurn {
            pos_x: self.pos_x,
            pos_y: self.pos_y,
            player_num: self.player_num,
            turn_num: self.turn_num
        }
    }
}