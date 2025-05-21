package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves {

    public BishopMoves(){}

    public static Collection<ChessMove> possibleBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> movable_places = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        ChessPosition currentIteration = new ChessPosition(y,x);
        boolean cont = true;

        while (x<8 && y<8 && cont){  //if at 8 then we're at the edge already and don't want to go any further
            x++;    //increment to new diagonal square
            y++;
            currentIteration.setColumn(x);    //set the square we're currently looking at
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x>1 && y>1 && cont){
            x--;
            y--;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x<8 && y>1 && cont){
            x++;
            y--;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }

        x = myPosition.getColumn();
        y = myPosition.getRow();
        cont = true;

        while (x>1 && y<8 && cont){
            x--;
            y++;
            currentIteration.setColumn(x);
            currentIteration.setRow(y);
            cont = PieceMovesCalculator.checkOtherPieces(board,myPosition,currentIteration,movable_places,null);
        }
        return movable_places;
    }
}
