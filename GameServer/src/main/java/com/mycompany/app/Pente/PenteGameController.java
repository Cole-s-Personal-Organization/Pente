package main.java.com.mycompany.app.Pente;

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

        this.playerCaptures = new int[8];
    }

    public void takePlayerTurn() {

        boolean gameWon = this.isGameWon();
    }

    private boolean isGameWon() {
        boolean winByInRow = this.checkConsecutiveInRowWinCond();
        boolean winByCaptures = this.checkCaptureWinCon();
        
        return winByCaptures || winByInRow; // placeholder
     }

    private boolean checkConsecutiveInRowWinCond() {
        return false;
    }  

    private boolean checkCaptureWinCon() {
        return false;
    }
    
}
