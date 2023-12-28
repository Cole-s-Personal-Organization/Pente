package main.java.com.mycompany.app.Pente;
import java.util.HashMap;

public class PenteGameController {
    private PenteGameSettings gameSettings;
    private PenteGameBoardModel model;

    private int[] playerCaptures;

    public PenteGameController(
        PenteGameSettings settings,
        PenteGameBoardModel model
    ) {
        if (this.gameSettings != null) {
            this.gameSettings = settings;
        } else {
            this.gameSettings = new PenteGameSettings.PenteGameSettingsBuilder().setToDefaultValues().build();
        }
        this.model = model;

        this.playerCaptures = new int[2];
    }

    /**
     * takePlayerTurn takes a player's turn and and and
     * @param turn object containing the player's chosen action on their turn
     */
    public void takePlayerTurn(PenteTurn turn) {
        model.setMove(turn);
        this.checkForCaptures(turn);
        boolean winByConsecutive = this.checkConsecutiveWinCon();
        boolean winByCaptures = this.checkCaptureWinCon();
        // if(gameWon) send a message to the lobby containing the winner and wincon
    }

    private int checkForCaptures(PenteTurn turn) {
        return 0;
    }

    private boolean checkConsecutiveWinCon() {
        return false;
    }

    private boolean checkCaptureWinCon() {
        return false;
    }
}
