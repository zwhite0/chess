package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves {

    public QueenMoves(){}

    public static Collection<ChessMove> possibleQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        PieceMovesCalculator.checkDiagonals(board,myPosition,movablePlaces);
        PieceMovesCalculator.checkLines(board,myPosition,movablePlaces);
        return movablePlaces;
    }
}

