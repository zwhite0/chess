package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves {

    public KnightMoves(){}

    public static Collection<ChessMove> possibleKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);

        x++;
        y= y + 2;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x = x - 2;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y--;
        x--;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y = y-2;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x++;
        y--;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x = x + 2;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y++;
        x++;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y = y + 2;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        return movablePlaces;
    }
}

