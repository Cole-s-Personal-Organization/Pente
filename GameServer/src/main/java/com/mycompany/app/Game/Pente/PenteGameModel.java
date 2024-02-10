package com.mycompany.app.Game.Pente;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a Pente Board.
 *
 * @author Dan
 * @version 1.0.0
 */
public class PenteGameModel {

    private PenteGameSettings gameSettings;
    private Integer[] playerCaptures;
    private PenteBoardIdentifierEnum[][] gameBoard;
    private PenteBoardIdentifierEnum winner;
    private int COLS = 19;
    private int ROWS = 19;

    /**
     * Default Construtor
     * Initializes all values to their default/game start values
     */
    public PenteGameModel() {
        this.gameBoard = new PenteBoardIdentifierEnum[this.ROWS][this.COLS];
        for (int i = 0; i < this.ROWS; i++) {
            for (int j = 0; j < this.COLS; j++) {
                gameBoard[i][j] = PenteBoardIdentifierEnum.EMPTY;
            }
        }
        this.winner = null;
        this.gameSettings = new PenteGameSettings.PenteGameSettingsBuilder().setToDefaultValues().build();
        Integer[] playerCapturesDefaultList = {0, 0};
        this.playerCaptures = playerCapturesDefaultList;
    }

    public PenteGameModel(Integer[][] board, PenteGameSettings settings, Integer[] playerCaptures) {
        if (board.length == this.ROWS && board[0].length == this.COLS) {
            PenteBoardIdentifierEnum[] boardEnumValues = PenteBoardIdentifierEnum.values();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    Integer currPassedInBoardValue = board[i][j];
                    this.gameBoard[i][j] = boardEnumValues[currPassedInBoardValue];
                }
            }
        }
        this.winner = null;
        this.gameSettings = settings;
        this.playerCaptures = playerCaptures;
    }

    /**
     * Checks if this player has won by capturing enough pieces this turn.
     * @param turn
     * @return true if the player has won, false otherwise
     */
    public boolean checkCaptureWinCon(PenteTurn turn) {
        int indexOfPlayer = getPlayerStoreIndexFromIdEnum(turn.getPlayerNumber());
        int numCaptured = playerCaptures[indexOfPlayer];
        boolean hasWon = numCaptured >= this.gameSettings.capturesToWin;
        if (hasWon) {
            this.winner = turn.getPlayerNumber();
        }
        return hasWon;
    }

    /**
     * This method removes pieces that were captured on this turn and increments the counter for the
     * number of pieces captured by this player's turn.
     * @param turn
     */
    public void removeCaptured(PenteTurn turn) {
        int numCaptured = 0;

        // the directions the helper function will increment in to check for captures
        int[][] incrementDirections = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

        for (int i = 1; i < 8; i++) {
            numCaptured += removeCapturedInDirection(turn, incrementDirections[i][0],
            incrementDirections[i][1]);
        }
        int indexOfPlayer = getPlayerStoreIndexFromIdEnum(turn.getPlayerNumber());
        this.playerCaptures[indexOfPlayer] += numCaptured;
    }

    /**
     * Helper method for removeCaptured that checks for captures in a specified direction and
     * removes those pieces.
     * For example, x_dir = 1 and y_dir = 0 would check for and remove captured pieces to the right
     * from the piece that was just placed and return the number of pieces captured.
     * @param turn
     * @param x_dir the x-increment for the direction of the search, should be -1, 0, or 1
     * @param y_dir the y-increment for the direction of the search, should be -1, 0, or 1
     * @return the number of pieces captured in the chosen direction
     */
    private int removeCapturedInDirection(PenteTurn turn, int x_dir, int y_dir) {
        int curr_x = turn.getPosX() + x_dir;
        int curr_y = turn.getPosY() + y_dir;
        List<int[]> removalCandidates = new ArrayList<>();
        while (curr_x >= 0 && curr_x < this.COLS && curr_y >= 0 && curr_y < this.ROWS &&
        this.gameBoard[curr_y][curr_x] != PenteBoardIdentifierEnum.EMPTY &&
        this.gameBoard[curr_y][curr_x] != turn.getPlayerNumber()) {
            int[] removalCandidate = {curr_x, curr_y};
            removalCandidates.add(removalCandidate);
            curr_x += x_dir;
            curr_y += y_dir;
        }
        if (curr_x >= 0 && curr_x < this.COLS && curr_y >= 0 && curr_y < this.ROWS &&
        this.gameBoard[curr_y][curr_x] == turn.getPlayerNumber()) {
            for (int i = 0; i < removalCandidates.size(); i++) {
                gameBoard[removalCandidates.get(i)[1]][removalCandidates.get(i)[0]] =
                PenteBoardIdentifierEnum.EMPTY;
            }
            return removalCandidates.size();
        }
        return 0;
    }

    /**
     * @param playerEnumId
     * @return the index of this player in the other data structures in this class
     */
    private Integer getPlayerStoreIndexFromIdEnum(PenteBoardIdentifierEnum playerEnumId) {
        ArrayList<PenteBoardIdentifierEnum> enumValues = new ArrayList<>(Arrays.asList(PenteBoardIdentifierEnum.values()));
        return enumValues.indexOf(playerEnumId) - 1;
    }

    /**
     * Checks if a turn is valid and performs it if it is. Throws an exception if turn is invalid.
     * @param turn
     * @throws InvalidTurnException
     */
    public void setMove(PenteTurn turn) throws InvalidTurnException{
        try {
            this.checkMove(turn);
        } catch (InvalidTurnException e) {
            throw e;
        }
        this.gameBoard[turn.getPosY()][turn.getPosX()] = turn.getPlayerNumber();
    }

    /**
     * Checks if a mock turn would be valid for a player to take. Throws various exceptions if
     * the turn is not valid.
     * @param turn
     */
    private void checkMove(PenteTurn turn) throws InvalidTurnException {
        if (this.winner != null) {
            throw new InvalidTurnException("Game is over; a player has already won.");
        }
        if (turn.getPosX() >= this.COLS || turn.getPosY() >= this.ROWS || turn.getPosX() < 0 || turn.getPosY() < 0) {
            throw new InvalidTurnException("Location out of bounds.");
        }
        if (gameBoard[turn.getPosY()][turn.getPosX()] != PenteBoardIdentifierEnum.EMPTY) {
            throw new InvalidTurnException("Location already occupied.");
        }
        if (!checkProSpecialRules(turn)) {
            throw new InvalidTurnException("Location does not conform to Pente pro ruleset.");
        }
        if (turn.getPlayerNumber() == PenteBoardIdentifierEnum.EMPTY) {
            throw new InvalidTurnException("Player number cannot be set to EMPTY.");
        }
    }

    /**
     * The pente pro ruleset has certain requirements for player 1's first two turns. This method
     * checks that a turn conforms to this ruleset.
     * @param turn
     * @return true if the move conforms to the pente pro ruleset, false otherwise
     */
    private boolean checkProSpecialRules(PenteTurn turn) {
        int proRuleSecondPositionOffset = 3;
        if (turn.isTurnOneAction != null && turn.isTurnOneAction) {
            if (gameBoard[this.ROWS / 2][this.COLS / 2] == PenteBoardIdentifierEnum.EMPTY) {
                if (turn.getPosX() == this.COLS / 2 && turn.getPosY() == this.ROWS / 2) {
                    return true;
                } else {
                    return false;
                }
            } else if (
                turn.getPosX() < this.COLS / 2 + proRuleSecondPositionOffset &&
                turn.getPosX() > this.COLS / 2 - proRuleSecondPositionOffset &&
                turn.getPosY() < this.ROWS / 2 + proRuleSecondPositionOffset &&
                turn.getPosY() > this.ROWS / 2 - proRuleSecondPositionOffset) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this turn has caused the player to win by having enough consecutive pieces
     * @param turn
     * @return true if the player has won, false otherwise
     */
    public boolean checkConsecutiveWinCon(PenteTurn turn) {
        int numInARowToWin = this.gameSettings.numInARowToWin;
        if (   checkNInADirection(turn, numInARowToWin, 1, 1)
            || checkNInADirection(turn, numInARowToWin, 1, 0)
            || checkNInADirection(turn, numInARowToWin, 1, -1)
            || checkNInADirection(turn, numInARowToWin, 0, 1)) {
            return true;
        }
        return false;
    }

    /**
     * Helper method for checkConsecutiveWinCon that checks if a player has enough pieces in a row
     * in a given direction to satisfy the win condition.
     * For example, passing in x_dir = 1 and y_dir = 0 will check if the player has enough pieces in
     * a row in the right/left direction from the piece they just played on this turn.
     * @param turn
     * @param n the number of pieces in a row required to win
     * @param x_dir the x-increment for the direction of the search, should be -1, 0, or 1
     * @param y_dir the y-increment for the direction of the search, should be -1, 0, or 1
     * @return true if the player has satisfied the win con in the given direction, false otherwise
     */
    private boolean checkNInADirection(PenteTurn turn, int n, int x_dir, int y_dir) {
        int counter = 1;
        int curr_x = turn.getPosX() + x_dir;
        int curr_y = turn.getPosY() + y_dir;
        while (curr_x >= 0 && curr_x < this.COLS && curr_y >= 0 && curr_y < this.ROWS &&
        this.gameBoard[curr_y][curr_x] == turn.getPlayerNumber()) {
            counter += 1;
            curr_x += x_dir;
            curr_y += y_dir;
        }
        curr_x = turn.getPosX() - x_dir;
        curr_y = turn.getPosY() - y_dir;
        while (curr_x >= 0 && curr_x < this.COLS && curr_y >= 0 && curr_y < this.ROWS &&
        this.gameBoard[curr_y][curr_x] == turn.getPlayerNumber()) {
            counter += 1;
            curr_x -= x_dir;
            curr_y -= y_dir;
        }
        return counter >= n;
    }

    public PenteBoardIdentifierEnum getGameBoardValueAtPosition(int posX, int posY) {
        return this.gameBoard[posY][posX];
    }
    public PenteBoardIdentifierEnum[][] getGameBoard() {
        return gameBoard;
    }
    public PenteGameSettings getGameSettings() {
        return gameSettings;
    }
    public Integer[] getPlayerCaptures() {
        return playerCaptures;
    }

    /**
     * Prints a formated version of the board
     */
    // @Override
    // public String toString() {
    //     String buildString = "";

    //     // initial catch for any error causing cases
    //     if (this.gameBoard == null || this.gameBoard.length <= 0) {
    //         return buildString;
    //     }

    //     String indent = "   "; // amount of indent to use to allow space for left side row labeling

    //     char[] colLetters = new char[this.gameBoard[0].length];
    //     for (int i = 0; i < this.gameBoard[0].length; i++) {
    //         colLetters[i] = (char)(97 + i);
    //     }


    //     buildString = buildString // col identifier letters
    //     .concat(indent)
    //     .concat("  ") // center the letters above each col
    //     .concat(
    //         Arrays.toString(colLetters)
    //             .replace(",", "  ")
    //             .replace("[", " ")
    //             .replace("]", "")
    //             .trim()
    //             .concat("\n")
    //     );

    //     for (int i = 0; i < colLetters.length; i++) {
    //         PenteBoardIdentifierEnum[] row = this.gameBoard[i];

    //         buildString = buildString // the dashed line above each row
    //             .concat(indent)
    //             .concat("----".repeat(row.length)
    //                 .concat("-"))
    //             .concat("\n");

    //         buildString = buildString  // initial row identifier
    //             .concat(String.valueOf(i))
    //             .concat(indent.substring(String.valueOf(i).length()));

    //         for (PenteBoardIdentifierEnum rowValue : row) { // the numbers and their dividers
    //             buildString = buildString
    //                 .concat("| ")
    //                 .concat(String.valueOf(rowValue))
    //                 .concat(" ");
    //         }

    //         buildString = buildString // end divider
    //             .concat("|\n");
    //     }
    //     buildString = buildString // the dashed line above each row
    //             .concat(indent)
    //             .concat("----".repeat(this.gameBoard[0].length)
    //                 .concat("-"))
    //             .concat("\n");
    //     return buildString;
    // }

    public class InvalidTurnException extends Exception {
        public InvalidTurnException(String e) {
            super(e);
        }
    }
}