package com.mycompany.app.Game.Pente;
import java.util.HashMap;
import java.util.UUID;

import com.mycompany.app.WebServer.Packet;


/**
 * A controller for the pente game.
 * 
 * @author Dan
 * @version 1.0.0
 */
public class PenteGameModel {
    private PenteGameSettings gameSettings;
    private PenteGameBoardModel board;

    private int[] playerCaptures;

    public PenteGameModel() {
        this.gameSettings = new PenteGameSettings.PenteGameSettingsBuilder().setToDefaultValues().build();
    }

    public PenteGameModel(
        PenteGameSettings settings,
        PenteGameBoardModel model
    ) {
        if (this.gameSettings != null) {
            this.gameSettings = settings;
        } else {
            this.gameSettings = new PenteGameSettings.PenteGameSettingsBuilder().setToDefaultValues().build();
        }
        this.board = model;

        this.playerCaptures = new int[2];
    }

    void start() {
        this.board = new PenteGameBoardModel();
    }

    
    // public void takePlayerTurn(PenteTurn turn) {
    //     model.setMove(turn);
    //     this.playerCaptures[turn.playerNumber] += model.removeCaptured(turn);
    //     boolean winByCaptures = this.checkCaptureWinCon(turn);
    //     // boolean winByConsecutive = model.checkConsecutiveWinCon(turn);
    //     // if(gameWon) send a message to the lobby containing the winner and wincon and the turn
    //     // else send turn to all, and prompt the next player for their turn
    // }

    // private boolean checkConsecutiveWinCon(PenteTurn turn) {
    //     return model.checkNInARow(turn, this.gameSettings.numInARowToWin);
    // }

    // private boolean checkCaptureWinCon(PenteTurn turn) {
    //     return (playerCaptures[turn.playerNumber] >= this.gameSettings.capturesToWin);
    // }
}
