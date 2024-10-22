package chess;

import chess.movecalculator.DirectionalMoves;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        PieceType piece = getPieceType();
        ChessGame.TeamColor color = getTeamColor();

//        System.out.println(piece);
        int max;
        switch (piece){
            case KING:
                max = 1;
                DirectionalMoves.upright(board, myPosition, color, moves, max);
                DirectionalMoves.downright(board, myPosition, color, moves, max);
                DirectionalMoves.downleft(board, myPosition, color, moves, max);
                DirectionalMoves.upleft(board, myPosition, color, moves, max);
                DirectionalMoves.up(board, myPosition, color, moves, max);
                DirectionalMoves.down(board, myPosition, color, moves, max);
                DirectionalMoves.right(board, myPosition, color, moves, max);
                DirectionalMoves.left(board, myPosition, color, moves, max);
                break;
            case QUEEN:
                max = 8;
                DirectionalMoves.upright(board, myPosition, color, moves, max);
                DirectionalMoves.downright(board, myPosition, color, moves, max);
                DirectionalMoves.downleft(board, myPosition, color, moves, max);
                DirectionalMoves.upleft(board, myPosition, color, moves, max);
                DirectionalMoves.up(board, myPosition, color, moves, max);
                DirectionalMoves.down(board, myPosition, color, moves, max);
                DirectionalMoves.right(board, myPosition, color, moves, max);
                DirectionalMoves.left(board, myPosition, color, moves, max);
                break;
            case BISHOP:
                max = 8;
                DirectionalMoves.upright(board, myPosition, color, moves, max);
                DirectionalMoves.downright(board, myPosition, color, moves, max);
                DirectionalMoves.downleft(board, myPosition, color, moves, max);
                DirectionalMoves.upleft(board, myPosition, color, moves, max);
                break;
            case KNIGHT:
                DirectionalMoves.knightmove(board, myPosition,color,moves);
                break;
            case ROOK:
                max = 8;
                DirectionalMoves.up(board, myPosition, color, moves, max);
                DirectionalMoves.down(board, myPosition, color, moves, max);
                DirectionalMoves.right(board, myPosition, color, moves, max);
                DirectionalMoves.left(board, myPosition, color, moves, max);
                break;
            case PAWN:
                DirectionalMoves.pawnmove(board, myPosition,color,moves);
                break;
        }
        return moves;
    }

    @Override
    public boolean equals(Object check) {
        if (this == check){
            return true;}
        if (check == null || getClass() != check.getClass()){
            return false;}

        ChessPiece piece = (ChessPiece) check;

        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
