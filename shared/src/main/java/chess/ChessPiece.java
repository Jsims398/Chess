package chess;

import chess.MoveCalculator.DirectionalMoves;

import java.util.Collection;
import java.util.HashSet;

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
        switch (piece){
            case KING:
                break;
            case QUEEN:
                break;
            case BISHOP:
                DirectionalMoves.UpRight(board, myPosition, color, moves);
                DirectionalMoves.DownRight(board, myPosition, color, moves);
                DirectionalMoves.DownLeft(board, myPosition, color, moves);
                DirectionalMoves.UpLeft(board, myPosition, color, moves);
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }





        return moves;
    }
}
