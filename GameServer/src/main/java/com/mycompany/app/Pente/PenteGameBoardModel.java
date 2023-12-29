package main.java.com.mycompany.app.Pente;

import java.util.Arrays;

public class PenteGameBoardModel {
    private int[][] gameBoard;
    private int cols;
    private int rows;

    public PenteGameBoardModel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        gameBoard = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameBoard[i][j] = 0;
            }
        }
    }

    public void setMove(PenteTurn turn) {
        this.checkMove(turn);
        this.gameBoard[turn.posY][turn.posX] = turn.playerNumber;
    }

    // we should rename isTurnOneAction or create an isTurnTwoAction variable
    // we should probably only allow odd numbers of rows/columns with the pro
    // ruleset, or i can add logic to support even numbers of rows/cols but that
    // would get very complicated without an isTurnTwoAction boolean
    private void checkMove(PenteTurn turn) {
        if (turn.posX > this.cols || turn.posY > this.rows || turn.posX < 1 || turn.posY < 1) {
            throw new InvalidTurnException("Location out of bounds.");
        }
        if (gameBoard[turn.posY][turn.posX] != 0) {
            throw new InvalidTurnException("Location already occupied.");
        }
        if (!checkProSpecialRules(turn)) {
            throw new InvalidTurnException("Location does not conform to Pente pro ruleset.");
        }
        return true;
    }

    private boolean checkProSpecialRules(PenteTurn turn) {
        if (turn.isTurnOneAction != null && turn.isTurnOneAction) {
            if (gameBoard[this.rows / 2][this.cols / 2] == 0) {
                if (turn.posX == this.cols / 2 && turn.poxY == this.rows / 2) {
                    return true;
                } else {
                    return false;
                }
            } else if (
                turn.posX < this.cols / 2 + 4 &&
                turn.posX > this.cols / 2 - 2 &&
                turn.poxY < this.rows / 2 + 4 &&
                turn.posY > this.rows / 2 - 2) {
                return false;
            }
        }
        return true;
    }

    public int removeCaptured(PenteTurn turn) {
        int numCaptured = 0;
        return numCaptured;
    }

    // TODO: actually implement some code
    public boolean checkNInARow(PenteTurn turn, int n) {
        return false;
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
            int[] row = this.gameBoard[i];

            buildString = buildString // the dashed line above each row
                .concat(indent)
                .concat("----".repeat(row.length)
                    .concat("-"))
                .concat("\n");

            buildString = buildString  // initial row identifier
                .concat(String.valueOf(i))
                .concat(indent.substring(String.valueOf(i).length()));

            for (int rowValue : row) { // the numbers and their dividers
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

class InvalidTurnException extends Exception {
    public InvalidTurnException() {
    }
    public InvalidTurnException(String message) {
        super(message);
    }
}
