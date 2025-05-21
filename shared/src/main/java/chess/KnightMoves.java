package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves {

    public KnightMoves(){}

    public static Collection<ChessMove> possibleKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movable_places = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);

        x++;
        y= y + 2;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x = x - 2;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y--;
        x--;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y = y-2;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x++;
        y--;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        x = x + 2;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y++;
        x++;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        y = y + 2;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movable_places, null);
        }

        return movable_places;
    }
}

