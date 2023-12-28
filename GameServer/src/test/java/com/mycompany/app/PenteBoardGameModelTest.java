package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mycompany.app.Game.Pente.*;

public class PenteBoardGameModelTest {
    
    @Test
    public void constructDefaultGameBoard() {
        PenteGameBoardModel model = new PenteGameBoardModel();
        assertNotNull(model);

        // ensure that all positions are set to "EMPTY"
        for (PenteBoardIdentifierEnum[] modelRow : model.getGameBoard()) {
            for (PenteBoardIdentifierEnum colValue : modelRow) {
                assertEquals(colValue, PenteBoardIdentifierEnum.EMPTY);
            }
        }
    }

    @Test
    public void basicSetMove() {
        PenteGameBoardModel model = new PenteGameBoardModel();
        
        // move at 5,5
        int posX = 5;
        int posY = 5;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();

        model.setMove(turn);
        PenteBoardIdentifierEnum storedValue = model.getGameBoardValueAtPosition(posX, posY);
        assertEquals(storedValue, playerNum);
    }

    @Test
    public void cornerSetMove() {
        PenteGameBoardModel model = new PenteGameBoardModel();

        // move at 0, 0
        int posXFirst = 5;
        int posYFirst = 5;
        PenteBoardIdentifierEnum playerNumFirst = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn firstTurn = new PenteTurn.PenteTurnBuilder(posXFirst, posYFirst, playerNumFirst).build();

        // move at 18, 18
        int posXSecond = 18;
        int posYSecond = 18;
        PenteBoardIdentifierEnum playerNumSecond = PenteBoardIdentifierEnum.PLAYER3;
        PenteTurn secondTurn = new PenteTurn.PenteTurnBuilder(posXSecond, posYSecond, playerNumSecond).build();


        model.setMove(firstTurn);
        model.setMove(secondTurn);
    }

    @Test(expected= IllegalArgumentException.class)
    public void illegalOnTopOfAnotherSetMove() {
        PenteGameBoardModel model = new PenteGameBoardModel();
        
        // move at 5,5
        int posXFirst = 5;
        int posYFirst = 5;
        PenteBoardIdentifierEnum playerNumFirst = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn firstTurn = new PenteTurn.PenteTurnBuilder(posXFirst, posYFirst, playerNumFirst).build();

        // move at 5,5
        int posXSecond = 5;
        int posYSecond = 5;
        PenteBoardIdentifierEnum playerNumSecond = PenteBoardIdentifierEnum.PLAYER3;
        PenteTurn secondTurn = new PenteTurn.PenteTurnBuilder(posXSecond, posYSecond, playerNumSecond).build();

        model.setMove(firstTurn);
        model.setMove(secondTurn);
        
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void illegalOutOfBoundsSetMove() {
        PenteGameBoardModel model = new PenteGameBoardModel();
        
        // move at 5,5
        int posX = 20;
        int posY = 20;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();

        model.setMove(turn);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalSetToEmptySetMove() {
        PenteGameBoardModel model = new PenteGameBoardModel();
        
        // move at 5,5
        int posX = 5;
        int posY = 5;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.EMPTY; // player num set to empty
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();

        model.setMove(turn);
    }
}
