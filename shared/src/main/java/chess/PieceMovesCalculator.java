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


    public static boolean CheckOtherPieces(ChessBoard board, ChessPosition myPosition, ChessPosition currentIteration, Collection<ChessMove> movable_places, ChessPiece.PieceType promotion){
        ChessPiece moving_piece = board.getPiece(myPosition);  //need moving piece so we know what color it is
        ChessPiece potential_move = board.getPiece(currentIteration);  //store a piece here to later check if there is a piece in this position
        int y = currentIteration.getRow();  //for creating a copy of currentIteration
        int x = currentIteration.getColumn();  //for creating a copy of currentIteration
        boolean cont = true;
        if (potential_move == null) {  //what to do if the potential space the piece could move to is empty
            ChessPosition movablePos = new ChessPosition(y,x);  //create copy of currentIteration so copy doesn't update when we don't want it to
            movable_places.add(new ChessMove(myPosition, movablePos, promotion));  //add copy to list of possible moves
        } else if (potential_move.getTeamColor().equals(moving_piece.getTeamColor())) { //what to do if there is a piece in the spot, and it is the same color
            cont = false;  //piece can't move here, stops the iteration from continuing
        } else {  //what to do if there is a piece of opposite color in currentIteration
            ChessPosition movablePos = new ChessPosition(y,x); //create copy
            movable_places.add(new ChessMove(myPosition, movablePos, promotion));  //add copy to list of possible moves
            cont = false;  //piece can't move past it so stop the iteration
        }
        return cont;
    }

}
