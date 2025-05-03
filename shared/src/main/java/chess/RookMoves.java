package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {

    public RookMoves(){}

    public static Collection<ChessMove> PossibleRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movable_places = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);
        boolean cont = true;

        //checks spaces to right of rook
        while (x<8 && cont){  //if at 8 then we just checked the edge and don't want to go further
            x++; //increment
            currentIteration.setColumn(x); //change the space we are looking at to check if there is currently a piece there
            cont = PieceMovesCalculator.CheckOtherPieces(board,myPosition,currentIteration,movable_places,null); //checks if space has a piece there and adds move to list if appropriate
        }

        x = myPosition.getColumn(); //reset x to where the rook is
        cont = true;  //ensure next while loop occurs

        //checks spaces to left of rook
        while (x>1 && cont){  //if at 1 then we just checked the edge and don't want to go further
            x--;  //deincrement
            currentIteration.setColumn(x);
            cont = PieceMovesCalculator.CheckOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }

        x = myPosition.getColumn();
        currentIteration.setColumn(x);
        cont = true;

        while (y>1 && cont){
            y--;
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.CheckOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }
        y = myPosition.getRow();
        cont = true;

        while (y<8 && cont){
            y++;
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.CheckOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }
        return movable_places;
    }
}
