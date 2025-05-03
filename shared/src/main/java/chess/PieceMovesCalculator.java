package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public PieceMovesCalculator(Collection<ChessMove> moves){}


    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece moving_piece = board.getPiece(myPosition);
        if (moving_piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)){
            return BishopMoves.PossibleBishopMoves(board,myPosition);
        }
        if (moving_piece.getPieceType().equals(ChessPiece.PieceType.ROOK)){
            return RookMoves.PossibleRookMoves(board,myPosition);
        }
        return null;
    }

    /**
    public static int CheckOtherPieces(ChessBoard board, ChessPosition myPosition, ){
        if (potential_move == null) {
            ChessPosition movablePos = new ChessPosition(y,x);
            movable_places.add(new ChessMove(myPosition, movablePos, null));
        } else if (potential_move.getTeamColor().equals(moving_piece.getTeamColor())) {
            y = 8;
        } else {
            ChessPosition movablePos = new ChessPosition(y,x);
            movable_places.add(new ChessMove(myPosition, movablePos, null));
            y = 8;
        }
    }
    */
}
