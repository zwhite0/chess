package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {

    public RookMoves(){}

    public static Collection<ChessMove> possibleRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        PieceMovesCalculator.checkLines(board,myPosition,movablePlaces);
        return movablePlaces;
    }
}
