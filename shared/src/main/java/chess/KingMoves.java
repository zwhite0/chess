package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves {

    KingMoves() {
    }

    public static Collection<ChessMove> possibleKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movable_places = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y, x);

        x++;
        y++;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x--;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }


        x--;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y--;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }


        y--;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x++;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x++;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y++;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        return movable_places;
    }
}
