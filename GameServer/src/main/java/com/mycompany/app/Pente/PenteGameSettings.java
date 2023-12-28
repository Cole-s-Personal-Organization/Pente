package main.java.com.mycompany.app.Pente;

public class PenteGameSettings {
    private int numInARowToWin;
    private int capturesToWin;
    
    private PenteGameSettings(PenteGameSettingsBuilder builder) {
        this.numInARowToWin = builder.numInARowToWin;
        this.capturesToWin = builder.capturesToWin;
    }

    public static class PenteGameSettingsBuilder {
        private int numInARowToWin;
        private int capturesToWin;

        public PenteGameSettingsBuilder() {
            this.numInARowToWin = 5;
            this.capturesToWin = 5;
        }
        
        public void setCapturesToWin(int capturesToWin) {
            this.capturesToWin = capturesToWin;
        }

        public void setNumInARowToWin(int numInARowToWin) {
            this.numInARowToWin = numInARowToWin;
        }

        public PenteGameSettings build() {
            return new PenteGameSettings(this);
        }
    }   
}
