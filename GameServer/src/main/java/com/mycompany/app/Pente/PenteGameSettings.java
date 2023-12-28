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

        public PenteGameSettingsBuilder setToDefaultValues() {
            this.numInARowToWin = 5;
            this.capturesToWin = 5;
            return this;
        }
        
        public PenteGameSettingsBuilder setCapturesToWin(int capturesToWin) {
            this.capturesToWin = capturesToWin;
            return this;
        }

        public PenteGameSettingsBuilder setNumInARowToWin(int numInARowToWin) {
            this.numInARowToWin = numInARowToWin;
            return this;
        }

        public PenteGameSettings build() {
            return new PenteGameSettings(this);
        }
    }   
}
