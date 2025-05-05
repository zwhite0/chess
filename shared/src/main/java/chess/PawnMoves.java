package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves {

    PawnMoves() {
    }

    public static Collection<ChessMove> PossiblePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movable_places = new ArrayList<>();
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
        ChessPiece potential_move = board.getPiece(currentIteration);
        if (myPosition.getRow() < 7 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() > 2 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            //move forward 1 logic
            if (potential_move == null) {  //what to do if the potential space the piece could move to is empty
                ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                movable_places.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
            }
            //attack right logic
            x++;
            if (x < 9) {
                currentIteration.setColumn(x);
                potential_move = board.getPiece(currentIteration);
                if (potential_move != null && !potential_move.getTeamColor().equals(movingPawn.getTeamColor())) {
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movable_places.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
                }
            }
            //attack left logic
            x = x - 2;
            if (x > 0) {
                currentIteration.setColumn(x);
                potential_move = board.getPiece(currentIteration);
                if (potential_move != null && !potential_move.getTeamColor().equals(movingPawn.getTeamColor())) {
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movable_places.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
                }
            }
            //move forward 2 if on starting location logic
            x++;
            currentIteration.setColumn(x);
            potential_move = board.getPiece(currentIteration);
            if (myPosition.getRow() == 2 && potential_move == null && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() == 7 && potential_move == null && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                    y++;
                }
                if (movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                    y--;
                }
                currentIteration.setRow(y);
                potential_move = board.getPiece(currentIteration);
                if (potential_move == null) {  //what to do if the potential space the piece could move to is empty
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movable_places.add(new ChessMove(myPosition, movablePos, null));  //add copy to list of possible moves
                }
            }
        }

        //promotion logic
        if (myPosition.getRow() == 7 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.WHITE) || myPosition.getRow() == 2 && movingPawn.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            //move forward 1 logic
            if (potential_move == null) {  //what to do if the potential space the piece could move to is empty
                ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.KNIGHT));
                movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.BISHOP));
                movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.ROOK));
                movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.QUEEN));//add copy to list of possible moves
            }
            //attack right logic
            x++;
            if (x < 9) {
                currentIteration.setColumn(x);
                potential_move = board.getPiece(currentIteration);
                if (potential_move != null && !potential_move.getTeamColor().equals(movingPawn.getTeamColor())) {
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.KNIGHT));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.BISHOP));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.ROOK));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.QUEEN));  //add copy to list of possible moves
                }
            }
            //attack left logic
            x = x - 2;
            if (x > 0) {
                currentIteration.setColumn(x);
                potential_move = board.getPiece(currentIteration);
                if (potential_move != null && !potential_move.getTeamColor().equals(movingPawn.getTeamColor())) {
                    ChessPosition movablePos = new ChessPosition(y, x);  //create copy of currentIteration so copy doesn't update when we don't want it to
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.KNIGHT));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.BISHOP));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.ROOK));
                    movable_places.add(new ChessMove(myPosition, movablePos, ChessPiece.PieceType.QUEEN));  //add copy to list of possible moves
                }
            }
        }
        return movable_places;
    }
}