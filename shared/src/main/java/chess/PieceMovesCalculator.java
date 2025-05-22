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

    public static void checkDiagonals (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movablePlaces){
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);
        boolean cont = true;

        while (x<8 && y<8 && cont){  //if at 8 then we're at the edge already and don't want to go any further
            x++;    //increment to new diagonal square
            y++;
            currentIteration.setColumn(x);    //set the square we're currently looking at
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x>1 && y>1 && cont){
            x--;
            y--;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x<8 && y>1 && cont){
            x++;
            y--;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x>1 && y<8 && cont){
            x--;
            y++;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }
    }

    public static void checkLines(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movablePlaces){
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);
        boolean cont = true;

        //checks spaces to right of rook
        while (x<8 && cont){  //if at 8 then we just checked the edge and don't want to go further
            x++; //increment
            currentIteration.setColumn(x); //change the space we are looking at to check if there is currently a piece there
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null); //checks if space has a piece there and adds move to list if appropriate
        }

        x = myPosition.getColumn(); //reset x to where the rook is
        cont = true;  //ensure next while loop occurs

        //checks spaces to left of rook
        while (x>1 && cont){  //if at 1 then we just checked the edge and don't want to go further
            x--;  //deincrement
            currentIteration.setColumn(x);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }

        x = myPosition.getColumn();
        currentIteration.setColumn(x);
        cont = true;

        while (y>1 && cont){
            y--;
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }
        y = myPosition.getRow();
        cont = true;

        while (y<8 && cont){
            y++;
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration, movablePlaces,null);
        }
    }

}
