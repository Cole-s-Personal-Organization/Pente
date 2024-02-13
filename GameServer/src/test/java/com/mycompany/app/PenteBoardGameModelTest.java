package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Assert;

import com.mycompany.app.Game.Pente.*;

public class PenteBoardGameModelTest {

    @Test
    public void constructDefaultGameBoard() {
        PenteGameModel model = new PenteGameModel();
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
        PenteGameModel model = new PenteGameModel();

        // move at 5,5
        int posX = 5;
        int posY = 5;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();
        try {
            model.setMove(turn);
        } catch (Exception e) {
            Assert.fail();
        }
        PenteBoardIdentifierEnum storedValue = model.getGameBoardValueAtPosition(posX, posY);
        assertEquals(storedValue, playerNum);
    }

    @Test
    public void cornerSetMove() {
        PenteGameModel model = new PenteGameModel();

        // move at 0, 0
        int posXFirst = 0;
        int posYFirst = 0;
        PenteBoardIdentifierEnum playerNumFirst = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn firstTurn = new PenteTurn.PenteTurnBuilder(posXFirst, posYFirst, playerNumFirst).build();

        // move at 18, 18
        int posXSecond = 18;
        int posYSecond = 18;
        PenteBoardIdentifierEnum playerNumSecond = PenteBoardIdentifierEnum.PLAYER3;
        PenteTurn secondTurn = new PenteTurn.PenteTurnBuilder(posXSecond, posYSecond, playerNumSecond).build();


        try {
            System.out.println("HELLO WORLD");
            model.setMove(firstTurn);
            model.setMove(secondTurn);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void illegalOnTopOfAnotherSetMove() {
        PenteGameModel model = new PenteGameModel();
        boolean thrown = false;

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

        try {
            model.setMove(firstTurn);
            model.setMove(secondTurn);
        } catch (PenteGameModel.InvalidTurnException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

    @Test
    public void illegalOutOfBoundsSetMove() {
        PenteGameModel model = new PenteGameModel();
        boolean thrown = false;

        // move at 5,5
        int posX = 20;
        int posY = 20;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();

        try {
            model.setMove(turn);
        } catch (PenteGameModel.InvalidTurnException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

    @Test
    public void illegalSetToEmptySetMove() {
        PenteGameModel model = new PenteGameModel();
        boolean thrown = false;

        // move at 5,5
        int posX = 5;
        int posY = 5;
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.EMPTY; // player num set to empty
        PenteTurn turn = new PenteTurn.PenteTurnBuilder(posX, posY, playerNum).build();

        try {
            model.setMove(turn);
        } catch (PenteGameModel.InvalidTurnException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

    @Test
    public void incompleteNInARow() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(9, 9, playerNum).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(9, 8, playerNum).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(9, 7, playerNum).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(9, 6, playerNum).build();
        boolean fiveInARow = false;
        boolean expectedValue = false;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            fiveInARow = model.checkConsecutiveWinCon(turn4);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(fiveInARow, expectedValue);
    }

    @Test
    public void completeNInARow() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(9, 9, playerNum).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(9, 8, playerNum).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(9, 7, playerNum).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(9, 5, playerNum).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(9, 6, playerNum).build();
        boolean fiveInARow = false;
        boolean expectedValue = true;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            fiveInARow = model.checkConsecutiveWinCon(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(fiveInARow, expectedValue);
    }

    @Test
    public void cornerInARow() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum playerNum = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(4, 4, playerNum).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(3, 3, playerNum).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(2, 2, playerNum).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(1, 1, playerNum).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(0, 0, playerNum).build();
        boolean fiveInARow = false;
        boolean expectedValue = true;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            fiveInARow = model.checkConsecutiveWinCon(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(fiveInARow, expectedValue);
    }

    @Test
    public void basicRemoveCaptured() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(6, 7, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(7, 7, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(8, 7, player1).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(5, 7, player2).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(9, 7, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 3;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.removeCaptured(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(6, 7), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(7, 7), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(8, 7), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void cornerRemoveCaptured() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(1, 1, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(2, 2, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(3, 3, player1).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(4, 4, player2).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(0, 0, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 3;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.removeCaptured(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(1, 1), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(2, 2), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(3, 3), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void oppositeCornerRemoveCaptured() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(17, 17, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(16, 16, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(15, 15, player1).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(14, 14, player2).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(18, 18, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 3;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.removeCaptured(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(17, 17), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(16, 16), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getGameBoardValueAtPosition(15, 15), PenteBoardIdentifierEnum.EMPTY);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void cornerShouldNotRemoveCaptured() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(1, 1, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(2, 2, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(3, 3, player1).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(4, 4, player1).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(0, 0, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 0;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.removeCaptured(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(1, 1), player1);
        assertEquals(model.getGameBoardValueAtPosition(2, 2), player1);
        assertEquals(model.getGameBoardValueAtPosition(3, 3), player1);
        assertEquals(model.getGameBoardValueAtPosition(4, 4), player1);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void shouldNotRemoveBecauseEmptySpot() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(7, 7, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(8, 7, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(5, 7, player2).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(9, 7, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 0;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.removeCaptured(turn4);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(7, 7), player1);
        assertEquals(model.getGameBoardValueAtPosition(8, 7), player1);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void shouldNotRemoveBecauseNotAdjacent() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(7, 7, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(8, 7, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(9, 7, player2).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(6, 7, player2).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(5, 7, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 0;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.removeCaptured(turn5);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(7, 7), player1);
        assertEquals(model.getGameBoardValueAtPosition(8, 7), player1);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }

    @Test
    public void removeMultipleCaptureDirections() {
        PenteGameModel model = new PenteGameModel();
        PenteBoardIdentifierEnum player1 = PenteBoardIdentifierEnum.PLAYER1;
        PenteBoardIdentifierEnum player2 = PenteBoardIdentifierEnum.PLAYER2;
        PenteBoardIdentifierEnum empty = PenteBoardIdentifierEnum.EMPTY;
        PenteTurn turn1 = new PenteTurn.PenteTurnBuilder(1, 0, player1).build();
        PenteTurn turn2 = new PenteTurn.PenteTurnBuilder(2, 0, player1).build();
        PenteTurn turn3 = new PenteTurn.PenteTurnBuilder(3, 0, player1).build();
        PenteTurn turn4 = new PenteTurn.PenteTurnBuilder(1, 1, player1).build();
        PenteTurn turn5 = new PenteTurn.PenteTurnBuilder(2, 2, player1).build();
        PenteTurn turn6 = new PenteTurn.PenteTurnBuilder(4, 0, player2).build();
        PenteTurn turn7 = new PenteTurn.PenteTurnBuilder(3, 3, player2).build();
        PenteTurn turn8 = new PenteTurn.PenteTurnBuilder(0, 2, player2).build();
        int expcetedP1NumCaptured = 0;
        int expectedP2NumCaptured = 5;
        try {
            model.setMove(turn1);
            model.setMove(turn2);
            model.setMove(turn3);
            model.setMove(turn4);
            model.setMove(turn5);
            model.setMove(turn6);
            model.setMove(turn7);
            model.setMove(turn8);
            model.removeCaptured(turn8);
        } catch (Exception e) {
            Assert.fail();
        }
        assertEquals(model.getGameBoardValueAtPosition(1, 0), empty);
        assertEquals(model.getGameBoardValueAtPosition(2, 0), empty);
        assertEquals(model.getGameBoardValueAtPosition(3, 0), empty);
        assertEquals(model.getGameBoardValueAtPosition(1, 1), empty);
        assertEquals(model.getGameBoardValueAtPosition(2, 2), empty);
        assertEquals(model.getPlayerCaptures()[0].intValue(), expcetedP1NumCaptured);
        assertEquals(model.getPlayerCaptures()[1].intValue(), expectedP2NumCaptured);
    }
}