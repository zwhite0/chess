package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves {

    public BishopMoves(){}

    public static Collection<ChessMove> possibleBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        PieceMovesCalculator.checkDiagonals(board, myPosition, movablePlaces);
        return movablePlaces;
    }
}
