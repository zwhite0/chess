package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves {

    PawnMoves() {
    }

    public static Collection<ChessMove> possiblePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y, x);
        ChessPiece movingPawn = board.getPiece(myPosition);
        if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            y++;
        }
        if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            y--;
        }
        currentIteration.setRow(y);
        ChessPiece potentialMove = board.getPiece(currentIteration);
        if (myPosition.getRow() < 7 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() > 2 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            //move forward 1 logic
            if (potentialMove == null) {  //what to do if the potential space the piece could move to is empty
                ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                movablePlaces.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
            }
            //attack right logic
            x++;
            if (x < 9) {
                pawnAttack(x,y,board,myPosition,movablePlaces);
            }
            //attack left logic
            x = x - 2;
            if (x > 0) {
                pawnAttack(x,y,board,myPosition,movablePlaces);
            }
            //move forward 2 if on starting location logic
            x++;
            currentIteration.setColumn(x);
            potentialMove = board.getPiece(currentIteration);
            if (myPosition.getRow() == 2 && potentialMove == null && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() == 7 && potentialMove == null && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                    y++;
                }
                if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                    y--;
                }
                currentIteration.setRow(y);
                potentialMove = board.getPiece(currentIteration);
                if (potentialMove == null) {  //what to do if the potential space the piece could move to is empty
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movablePlaces.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
                }
            }
        }

        //promotion logic
        if (myPosition.getRow() == 7 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() == 2 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            //move forward 1 logic
            if (potentialMove == null) {  //what to do if the potential space the piece could move to is empty
                addAllPromotions(x,y,movablePlaces,myPosition);
            }
            //attack right logic
            x++;
            if (x < 9) {
                currentIteration.setColumn(x);
                potentialMove = board.getPiece(currentIteration);
                if (potentialMove != null && !potentialMove.getTeamColor().equals(movingPawn.getTeamColor())) {
                    addAllPromotions(x,y,movablePlaces,myPosition);
                }
            }
            //attack left logic
            x = x - 2;
            if (x > 0) {
                currentIteration.setColumn(x);
                potentialMove = board.getPiece(currentIteration);
                if (potentialMove != null && !potentialMove.getTeamColor().equals(movingPawn.getTeamColor())) {
                    addAllPromotions(x,y,movablePlaces,myPosition);
                }
            }
        }
        return movablePlaces;
    }

    private static void addAllPromotions(int x, int y, Collection<ChessMove> movablePlaces, ChessPosition myPosition){
        ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
        movablePlaces.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.KNIGHT));
        movablePlaces.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.BISHOP));
        movablePlaces.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.ROOK));
        movablePlaces.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.QUEEN));
    }

    private static void pawnAttack(int x, int y, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movablePlaces) {
        ChessPosition currentIteration = new ChessPosition(y, x);
        ChessPiece movingPawn = board.getPiece(myPosition);
        ChessPiece potentialMove = board.getPiece(currentIteration);
        if (potentialMove != null && !potentialMove.getTeamColor().equals(movingPawn.getTeamColor())) {
            ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
            movablePlaces.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
        }
    }
}