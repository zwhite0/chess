package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor currentTeamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeamTurn == chessGame.currentTeamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentTeamTurn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard currentBoard = getBoard();
        ChessBoard updatedBoard = new ChessBoard();
        ChessPiece[][] currentSquares = currentBoard.getSquares();
        ChessPiece[][] updatedSquares = updatedBoard.getSquares();

        for (int i = 0; i < updatedSquares.length; i++){
            for (int j = 0; j < updatedSquares[i].length; j++){
                updatedSquares[i][j] = currentSquares[i][j];
            }
        }

        ChessPiece currentPiece = currentBoard.getPiece(startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();
        Collection<ChessMove> fullMovesList = new ArrayList<>();
        if (currentPiece != null) {
            fullMovesList = currentPiece.pieceMoves(currentBoard, startPosition);
        }
        for (ChessMove move : fullMovesList){
            ChessPosition newPosition = move.getEndPosition();
            ChessPosition oldPosition = move.getStartPosition();
            if (move.getPromotionPiece()==null) {
                updatedBoard.addPiece(newPosition, currentPiece);
                updatedBoard.addPiece(oldPosition, null);
            } else {
                updatedBoard.addPiece(newPosition, new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
                updatedBoard.addPiece(oldPosition, null);
            }
            setBoard(updatedBoard);
            if (! isInCheck(currentPiece.getTeamColor())){
                validMoveList.add(move);
            }

            setBoard(currentBoard);

            for (int i = 0; i < updatedSquares.length; i++){
                for (int j = 0; j < updatedSquares[i].length; j++){
                    updatedSquares[i][j] = currentSquares[i][j];
                }
            }
        }
        return validMoveList;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition newPosition = move.getEndPosition();
        ChessPosition oldPosition = move.getStartPosition();
        ChessBoard currentBoard = getBoard();
        ChessPiece currentPiece = currentBoard.getPiece(oldPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (currentPiece != null) {
            validMoves = validMoves(oldPosition);
        }
        boolean isInValidMoves = false;

        for (ChessMove validMove : validMoves){
            if (validMove.equals(move)){
                isInValidMoves = true;
            }
        }

        if (currentPiece == null || isInCheck(currentPiece.getTeamColor()) || ! isInValidMoves || ! currentPiece.getTeamColor().equals(getTeamTurn())){
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece()==null) {
            currentBoard.addPiece(newPosition, currentPiece);
            currentBoard.addPiece(oldPosition, null);
        } else {
            currentBoard.addPiece(newPosition, new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
            currentBoard.addPiece(oldPosition, null);
        }
        setBoard(currentBoard);

        if (getTeamTurn()==TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard currentBoard = getBoard();
        ChessPiece[][] squares = currentBoard.getSquares();
        ChessPosition kingLocation = findKing(teamColor);  //sets the location of the king to check if any opposing pieces sees that location
        Collection<ChessMove> movablePlaces = new ArrayList<>();
        int x = 0;
        int y = 0;
        for (ChessPiece[] row : squares) {
            y++;
            x=0;
            for (ChessPiece square : row){
                x++;
                if (square != null && ! square.getTeamColor().equals(teamColor)){
                    movablePlaces.clear();
                    movablePlaces = square.pieceMoves(currentBoard,new ChessPosition(y,x));
                    for (ChessMove move : movablePlaces){
                        if (move.getEndPosition().equals(kingLocation)){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public ChessPosition findKing(TeamColor teamColor){
        ChessBoard currentBoard = getBoard();
        ChessPiece[][] squares = currentBoard.getSquares();
        int x = 0;
        int y = 0;
        for (ChessPiece[] row : squares) {
            y++;
            x=0;
            for (ChessPiece square : row) {
                x++;
                if (square != null) {
                    if (square.getPieceType().equals(ChessPiece.PieceType.KING) && square.getTeamColor().equals(teamColor)) {
                        return new ChessPosition(y, x);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean possibleMove = false;
        ChessBoard currentBoard = getBoard();
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int x=1; x<9; x++){
            for (int y=1; y<9;y++){
                ChessPosition currentPosition = new ChessPosition(y,x);
                ChessPiece currentPiece = currentBoard.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor().equals(teamColor)) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition);
                    allMoves.addAll(validMoves);
                }
            }
        }
        if (! allMoves.isEmpty()){
            possibleMove = true;
        }

        if (isInCheck(teamColor) && ! possibleMove){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
