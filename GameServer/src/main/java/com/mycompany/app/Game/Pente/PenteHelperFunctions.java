package com.mycompany.app.Game.Pente;

public class PenteHelperFunctions {

    /**
     * Converts a player enum to what it is represented as on the pente board
     * @param playerIdentifierEnum
     * @return
     */
    public static PenteBoardIdentifierEnum mapPlayerEnumIdentifierToBoardEnumIdentifier(PentePlayerIdentifierEnum playerIdentifierEnum) {
        PenteBoardIdentifierEnum boardEnum;

        switch (playerIdentifierEnum) {
            case PLAYER1:
                boardEnum = PenteBoardIdentifierEnum.PLAYER1;
                break;
            case PLAYER2:
                boardEnum = PenteBoardIdentifierEnum.PLAYER2;
                break;
            case PLAYER3:
                boardEnum = PenteBoardIdentifierEnum.PLAYER3;
                break;
            case PLAYER4:
                boardEnum = PenteBoardIdentifierEnum.PLAYER4;
                break;
            default:
                boardEnum = null;
                break;
        }

        return boardEnum;
    }

    public static PentePlayerIdentifierEnum mapBoardEnumIdentifierToPlayerEnumIdentifier(PenteBoardIdentifierEnum boardIdentifierEnum) {
        PentePlayerIdentifierEnum playerIdentifierEnum;

        switch (boardIdentifierEnum) {
            case PLAYER1:
                playerIdentifierEnum = PentePlayerIdentifierEnum.PLAYER1;
                break;
            case PLAYER2:
                playerIdentifierEnum = PentePlayerIdentifierEnum.PLAYER2;
                break;
            case PLAYER3:
                playerIdentifierEnum = PentePlayerIdentifierEnum.PLAYER3;
                break;
            case PLAYER4:
                playerIdentifierEnum = PentePlayerIdentifierEnum.PLAYER4;
                break;
            default:
                playerIdentifierEnum = null;
                break;
        }

        return playerIdentifierEnum;
    }
}
