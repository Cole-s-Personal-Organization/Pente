package com.mycompany.app.Game.Pente;

/**
 * Used to identify a players identity in a numerical format on the board
 * Right now, Maximum of four players are allowed so only four player enums are used
 */
public enum PenteBoardIdentifierEnum {
    EMPTY {
        @Override
        public String toString() {
            return " ";
        }
    }, 
    PLAYER1 {
        @Override
        public String toString() {
            return "1";
        }
        
    }, 
    PLAYER2 {
        @Override
        public String toString() {
            return "2";
        }
    }, 
    PLAYER3 {
        @Override
        public String toString() {
            return "3";
        }
    }, 
    PLAYER4 {
        @Override
        public String toString() {
            return "4";
        }
    }
}
