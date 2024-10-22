package chess.movecalculator;

import chess.*;
import java.util.HashSet;

public class DirectionalMoves {
    private static final int BOARD_HEIGHT = 8;
    private static final int BOARD_WIDTH = 8;

    private static void addMovesInDirection(
            ChessBoard board, ChessPosition position, ChessGame.TeamColor color,
            HashSet<ChessMove> moves, int rowshift, int colchift, int max
    ) {
        int pieceRow = position.getRow();
        int pieceCol = position.getColumn();
        int k = 0;

        for (int i = pieceRow + rowshift, j = pieceCol + colchift;
             isValidPosition(i, j);
             i += rowshift, j += colchift) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, j);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            if (pieceAtNewPosition == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (pieceAtNewPosition.getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void down(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, -1, 0, max);
    }
    public static void up(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, 1, 0, max);
    }
    public static void left(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, 0, -1, max);
    }
    public static void right(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, 0, 1, max);
    }
    public static void upright(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, 1, 1, max);
    }
    public static void upleft(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, 1, -1, max);
    }
    public static void downleft(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, -1, -1, max);
    }
    public static void downright(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        addMovesInDirection(board, position, color, moves, -1, 1, max);
    }

    public static void knightmove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves) {
        int pieceRow = position.getRow();
        int pieceCol = position.getColumn();
        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : possibleMoves) {
            int newRow = pieceRow + move[0];
            int newCol = pieceCol + move[1];

            ChessPosition newPosition = new ChessPosition(newRow, newCol);

            if (board.isValidPosition(newRow, newCol)) {
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != color) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
    }

    public static void pawnmove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves) {
        int pieceRow = position.getRow();
        int pieceCol = position.getColumn();
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition forwardMove = new ChessPosition(pieceRow + direction, pieceCol);

        if (board.isValidPosition(forwardMove.getRow(), forwardMove.getColumn()) && board.getPiece(forwardMove) == null) {
            if (isPromotionRow(forwardMove.getRow(), color)) {
                addPromotionMoves(moves, position, forwardMove);
            } else {
                moves.add(new ChessMove(position, forwardMove, null));
            }

            if ((color == ChessGame.TeamColor.BLACK && pieceRow == 7) || (color == ChessGame.TeamColor.WHITE && pieceRow == 2)) {
                ChessPosition doubleStepPosition = new ChessPosition(pieceRow + 2 * direction, pieceCol);

                if (board.isValidPosition(doubleStepPosition.getRow(), doubleStepPosition.getColumn()) && board.getPiece(doubleStepPosition) == null) {
                    moves.add(new ChessMove(position, doubleStepPosition, null));
                }
            }
        }

        int[][] sideAttack = {{direction, -1}, {direction, 1}};

        for (int[] move : sideAttack) {
            int newRow = pieceRow + move[0];
            int newCol = pieceCol + move[1];

            ChessPosition attackPosition = new ChessPosition(newRow, newCol);

            if (board.isValidPosition(newRow, newCol)) {
                ChessPiece pieceAttackedPosition = board.getPiece(attackPosition);

                if (pieceAttackedPosition != null && pieceAttackedPosition.getTeamColor() != color) {
                    if (isPromotionRow(newRow, color)) {
                        addPromotionMoves(moves, position, attackPosition);
                    } else {
                        moves.add(new ChessMove(position, attackPosition, null));
                    }
                }
            }
        }
    }

    private static boolean isPromotionRow(int row, ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE && row == 8) || (color == ChessGame.TeamColor.BLACK && row == 1);
    }

    private static void addPromotionMoves(HashSet<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
    }

    private static boolean isValidPosition(int row, int col) {
        return row > 0 && row <= BOARD_HEIGHT && col > 0 && col <= BOARD_WIDTH;
    }
}
