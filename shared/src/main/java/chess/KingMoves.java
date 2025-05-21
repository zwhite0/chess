package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves {

    KingMoves() {
    }

    public static Collection<ChessMove> possibleKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y, x);

        x++;
        y++;
        currentIteration.setRow(y);
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x--;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }


        x--;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y--;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }


        y--;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x++;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        x++;
        currentIteration.setColumn(x);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        y++;
        currentIteration.setRow(y);
        if (x>0 && x<9 && y>0 && y<9) {
            PieceMovesCalculator.checkOtherPieces(board, myPosition, currentIteration, movablePlaces, null);
        }

        return movablePlaces;
    }
}
