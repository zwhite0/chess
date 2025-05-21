package chess;

import java.util.Collection;

public class PieceMovesCalculator {

    public PieceMovesCalculator(Collection<ChessMove> moves){}


    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece movingPiece = board.getPiece(myPosition);
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.BISHOP)){
            return BishopMoves.possibleBishopMoves(board,myPosition);
        }
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.ROOK)){
            return RookMoves.possibleRookMoves(board,myPosition);
        }
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.QUEEN)) {
            return QueenMoves.possibleQueenMoves(board, myPosition);
        }
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            return KnightMoves.possibleKnightMoves(board, myPosition);
        }
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.PAWN)){
            return PawnMoves.possiblePawnMoves(board,myPosition);
        }
        if (movingPiece.getPieceType().equals(ChessPiece.PieceType.KING)){
            return KingMoves.possibleKingMoves(board,myPosition);
        }
        return null;
    }


    public static boolean checkOtherPieces(ChessBoard board, ChessPosition myPosition, ChessPosition currentIteration, Collection<ChessMove> movablePlaces, ChessPiece.PieceType promotion){
        ChessPiece movingPiece = board.getPiece(myPosition);  //need moving piece so we know what color it is
        ChessPiece potentialMove = board.getPiece(currentIteration);  //store a piece here to later check if there is a piece in this position
        int y = currentIteration.getRow();  //for creating a copy of currentIteration
        int x = currentIteration.getColumn();  //for creating a copy of currentIteration
        boolean cont = true;
        if (potentialMove == null) {  //what to do if the potential space the piece could move to is empty
            ChessPosition movablePos = new ChessPosition(y,x);  //create copy of currentIteration so copy doesn't update when we don't want it to
            movablePlaces.add(new ChessMove(myPosition, movablePos, promotion));  //add copy to list of possible moves
        } else if (potentialMove.getTeamColor().equals(movingPiece.getTeamColor())) { //what to do if there is a piece in the spot, and it is the same color
            cont = false;  //piece can't move here, stops the iteration from continuing
        } else {  //what to do if there is a piece of opposite color in currentIteration
            ChessPosition movablePos = new ChessPosition(y,x); //create copy
            movablePlaces.add(new ChessMove(myPosition, movablePos, promotion));  //add copy to list of possible moves
            cont = false;  //piece can't move past it so stop the iteration
        }
        return cont;
    }

}
