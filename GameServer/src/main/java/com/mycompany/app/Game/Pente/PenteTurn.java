
package com.mycompany.app.Game.Pente;


/**
 * A representation of Pente game turn.
 * 
 * @author Dan, Cole
 * @version 1.0.0
 */
public class PenteTurn {
    // required
    public final int posX;
    public final int posY;
    public final PenteBoardIdentifierEnum playerNumber;

    // optional
    public final Boolean isTurnOneAction;

    private PenteTurn(PenteTurnBuilder builder) {
        this.posX = builder.posX;
        this.posY = builder.posY;
        this.playerNumber = builder.playerNumber;
        this.isTurnOneAction = builder.isTurnOneAction;

    }

    /**
     * A builder object which supports the building of a pente game turn object.
     * 
     * @author Dan, Cole
     * @version 1.0.0
     */
    public static class PenteTurnBuilder {
        // required
        private int posX;
        private int posY;
        private PenteBoardIdentifierEnum playerNumber;

        // optional
        private boolean isTurnOneAction;

        public PenteTurnBuilder(int posX, int posY, PenteBoardIdentifierEnum playerNumber) {
            this.posX = posX;
            this.posY = posY;
            this.playerNumber = playerNumber;
        }

        public PenteTurnBuilder setTurnOneAction(boolean isTurnOneAction) {
            this.isTurnOneAction = isTurnOneAction;
            return this;
        }

        public PenteTurn build() {
            return new PenteTurn(this);
        }
    }
}
