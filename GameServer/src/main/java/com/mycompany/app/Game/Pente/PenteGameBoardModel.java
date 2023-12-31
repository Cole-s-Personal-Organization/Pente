package com.mycompany.app.Game.Pente;

import java.util.Arrays;

public class PenteGameBoardModel {
    private PenteBoardIdentifierEnum[][] gameBoard;

    public PenteGameBoardModel() {
        int cols = 19;
        int rows = 19;
        gameBoard = new PenteBoardIdentifierEnum[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameBoard[i][j] = PenteBoardIdentifierEnum.EMPTY;
            }
        }
    }
    // TODO check for captures
    public void setMove(PenteTurn turn) {
        this.checkMove(turn);
        this.gameBoard[turn.posY][turn.posX] = turn.playerNumber;
    }
    // TODO check if a move is valid
    private boolean checkMove(PenteTurn turn) {
        return false;
    }

    public int removeCaptured(PenteTurn turn) {
        int numCaptured = 0;
        return numCaptured;
    }

    public boolean checkNInARow(PenteTurn turn, int n) {
        return false;
    }

    // right now getters are being used for testing purposes
    public PenteBoardIdentifierEnum getGameBoardValueAtPosition(int posX, int posY) {
        return this.gameBoard[posY][posX];
    }
    public PenteBoardIdentifierEnum[][] getGameBoard() {
        return gameBoard;
    }

    @Override
    public String toString() {
        String buildString = "";

        // initial catch for any error causing cases
        if (this.gameBoard == null || this.gameBoard.length <= 0) {
            return buildString;
        }

        String indent = "   "; // amount of indent to use to allow space for left side row labeling

        char[] colLetters = new char[this.gameBoard[0].length];
        for (int i = 0; i < this.gameBoard[0].length; i++) {
            colLetters[i] = (char)(97 + i);
        }


        buildString = buildString // col identifier letters
        .concat(indent)
        .concat("  ") // center the letters above each col
        .concat(
            Arrays.toString(colLetters)
                .replace(",", "  ")
                .replace("[", " ")
                .replace("]", "")
                .trim()
                .concat("\n")
        );

        for (int i = 0; i < colLetters.length; i++) {
            PenteBoardIdentifierEnum[] row = this.gameBoard[i];

            buildString = buildString // the dashed line above each row
                .concat(indent)
                .concat("----".repeat(row.length)
                    .concat("-"))
                .concat("\n");

            buildString = buildString  // initial row identifier
                .concat(String.valueOf(i))
                .concat(indent.substring(String.valueOf(i).length()));

            for (PenteBoardIdentifierEnum rowValue : row) { // the numbers and their dividers
                buildString = buildString
                    .concat("| ")
                    .concat(String.valueOf(rowValue))
                    .concat(" ");
            }

            buildString = buildString // end divider
                .concat("|\n");
        }
        buildString = buildString // the dashed line above each row
                .concat(indent)
                .concat("----".repeat(this.gameBoard[0].length)
                    .concat("-"))
                .concat("\n");
        return buildString;
    }
}
