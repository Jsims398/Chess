package chess;

import chess.MoveCalculator.DirectionalMoves;

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
                DirectionalMoves.UpRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownLeft(board, myPosition, color, moves, max);
                DirectionalMoves.UpLeft(board, myPosition, color, moves, max);
                DirectionalMoves.Up(board, myPosition, color, moves, max);
                DirectionalMoves.Down(board, myPosition, color, moves, max);
                DirectionalMoves.Right(board, myPosition, color, moves, max);
                DirectionalMoves.Left(board, myPosition, color, moves, max);
                break;
            case QUEEN:
                max = 8;
                DirectionalMoves.UpRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownLeft(board, myPosition, color, moves, max);
                DirectionalMoves.UpLeft(board, myPosition, color, moves, max);
                DirectionalMoves.Up(board, myPosition, color, moves, max);
                DirectionalMoves.Down(board, myPosition, color, moves, max);
                DirectionalMoves.Right(board, myPosition, color, moves, max);
                DirectionalMoves.Left(board, myPosition, color, moves, max);
                break;
            case BISHOP:
                max = 8;
                DirectionalMoves.UpRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownRight(board, myPosition, color, moves, max);
                DirectionalMoves.DownLeft(board, myPosition, color, moves, max);
                DirectionalMoves.UpLeft(board, myPosition, color, moves, max);
                break;
            case KNIGHT:
                DirectionalMoves.KnightMove(board, myPosition,color,moves);
                break;
            case ROOK:
                max = 8;
                DirectionalMoves.Up(board, myPosition, color, moves, max);
                DirectionalMoves.Down(board, myPosition, color, moves, max);
                DirectionalMoves.Right(board, myPosition, color, moves, max);
                DirectionalMoves.Left(board, myPosition, color, moves, max);
                break;
            case PAWN:
                DirectionalMoves.PawnMove(board, myPosition,color,moves);
                break;
        }
        return moves;
    }

    @Override
    public boolean equals(Object check) {
        if (this == check)
            return true;
        if (check == null || getClass() != check.getClass())
            return false;

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
