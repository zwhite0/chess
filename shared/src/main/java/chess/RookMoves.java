package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {

    public RookMoves(){}

    public static Collection<ChessMove> PossibleRookMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece moving_piece = board.getPiece(myPosition);
        Collection<ChessMove> movable_places = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);
        while (x<8){  //if at 8 then we're at the edge already and don't want to go any further
            x++;    //increment to new column
            currentIteration.setColumn(x);    //set the square we're currently looking at
            ChessPiece potential_move = board.getPiece(currentIteration);  //see if there is a piece in the square we are looking at
            if (potential_move == null) {   //if space is empty add it to possible moves
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
            } else if (potential_move.getTeamColor().equals(moving_piece.getTeamColor())) {     //if space is taken by piece of same color then end iteration (cant pass)
                x = 8;
            } else {      //if space is taken by opposite color then add this space to movable spaces then end iteration (can't pass)
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
                x = 8;
            }
        }
        x = myPosition.getColumn();
        while (x>1){
            x--;
            currentIteration.setColumn(x);
            ChessPiece potential_move = board.getPiece(currentIteration);
            if (potential_move == null) {
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
            } else if (potential_move.getTeamColor().equals(moving_piece.getTeamColor())) {
                x = 1;
            } else {
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
                x = 1;
            }
        }
        x = myPosition.getColumn();
        currentIteration.setColumn(x);
        while (y>1){
            y--;
            currentIteration.setRow(y);
            ChessPiece potential_move = board.getPiece(currentIteration);
            if (potential_move == null) {
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
            } else if (potential_move.getTeamColor().equals(moving_piece.getTeamColor())) {
                y = 1;
            } else {
                ChessPosition movablePos = new ChessPosition(y,x);
                movable_places.add(new ChessMove(myPosition, movablePos, null));
                y = 1;
            }
        }
        y = myPosition.getRow();
        while (y<8){
            y++;
            currentIteration.setRow(y);
            ChessPiece potential_move = board.getPiece(currentIteration);
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
        return movable_places;
    }
}
