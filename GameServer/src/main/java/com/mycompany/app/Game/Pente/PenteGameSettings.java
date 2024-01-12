package com.mycompany.app.Game.Pente;

/**
 * A representation of Pente game settings.
 * 
 * @author Dan, Cole
 * @version 1.0.0
 */
public class PenteGameSettings {
    public final int numInARowToWin;
    public final int capturesToWin;
    
    private PenteGameSettings(PenteGameSettingsBuilder builder) {
        this.numInARowToWin = builder.numInARowToWin;
        this.capturesToWin = builder.capturesToWin;
    }

    /**
     * A builder object which supports the building of a pente game settings object.
     * 
     * @author Dan, Cole
     * @version 1.0.0
     */
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
