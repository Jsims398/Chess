package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is-
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        OBSERVER;

        public String toString(){
            if(this == WHITE){
                return "white";
            }
            else{
                return "black";
            }
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece current = board.getPiece(startPosition);
        if(current == null){
            return null;
        }
        HashSet<ChessMove> pMoves = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        HashSet<ChessMove> vMoves = new HashSet<>(pMoves.size());

        for(ChessMove move : pMoves){
            if (isValid(current, startPosition, move)) {
                vMoves.add(move);
            }
        }

        return vMoves;
    }
    private boolean isValid(ChessPiece current, ChessPosition start, ChessMove move) {
        ChessPiece tempPiece = board.getPiece(move.getEndPosition());
        board.addPiece(start, null);
        board.addPiece(move.getEndPosition(), current);
        boolean valid = !isInCheck(current.getTeamColor());
        board.addPiece(move.getEndPosition(), tempPiece);
        board.addPiece(start, current);
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean teamsTurn = getTeamTurn() == board.getTeamPosition(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());


        if(validMoves == null){
            throw new InvalidMoveException("No valid moves");
        }
        boolean validMove = validMoves.contains(move);

        if(validMove && teamsTurn){
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getStartPosition(),null);//remove old spot
            board.addPiece(move.getEndPosition(), piece);
            setTeamTurn(getTeamTurn() == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
        }
        else{
            throw new InvalidMoveException(String.format("was the move valid: %b was it your turn: %b", validMove, teamsTurn));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        TeamColor enemyColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (attackingKing(currentPiece, enemyColor,board, currentPosition, kingPosition)){
                    return true;
                }
            }
        }return false;
    }

    private boolean attackingKing(ChessPiece piece, TeamColor enemyColor, ChessBoard board, ChessPosition position, ChessPosition kingPosition) {
        if (piece != null && piece.getTeamColor() == enemyColor) {
            Collection<ChessMove> moves = piece.pieceMoves(board, position);
            return moves.stream().anyMatch(move -> move.getEndPosition().equals(kingPosition));
        }
        return false;
    }


    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor &&
                        currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i, j);
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
        if (!isInCheck(teamColor)) {
            return false;
        }
        return check(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return check(teamColor);
    }

    public boolean check(TeamColor teamColor){
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = board.getPiece(currentPosition);

                if (currentPiece != null && teamColor == currentPiece.getTeamColor()) {
                    Collection<ChessMove> moves = validMoves(currentPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        return board;
    }


    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + turn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}