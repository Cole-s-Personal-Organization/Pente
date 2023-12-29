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
        this.playerCaptures[turn.platerNumber] += model.removeCaptured(turn);
        boolean winByCaptures = this.checkCaptureWinCon();
        boolean winByConsecutive = model.checkConsecutiveWinCon();
        // if(gameWon) send a message to the lobby containing the winner and wincon and the turn
        // else send turn to all, and prompt the next player for their turn
    }

    private boolean checkConsecutiveWinCon(PenteTurn turn) {
        return model.checkNInARow(turn, this.gameSettings.numInARowToWin);
    }

    private boolean checkCaptureWinCon(PenteTurn turn) {
        return (playerCaptures[turn.playerNumber] >= settings.capturesToWin);
    }
}
