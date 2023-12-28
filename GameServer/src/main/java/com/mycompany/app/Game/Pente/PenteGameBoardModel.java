package main.java.com.mycompany.app.Pente;

import java.util.Arrays;

public class PenteGameBoardModel {
    private int[][] gameBoard;

    public PenteGameBoardModel() {
        int cols = 19;
        int rows = 19;
        gameBoard = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameBoard[i][j] = 0;
            }
        }
    }

    public void setMove(PenteTurn turn) {
        this.checkMove(turn);
    }

    private boolean checkMove(PenteTurn turn) {
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
