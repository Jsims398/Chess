package chess.MoveCalculator;

import chess.*;

import java.util.HashSet;

public class DirectionalMoves {
    private static int BoardHight = 8;
    private static int BoardWidth = 8;


    public static void Down(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();

        int k = 0;
        for (int i = PieceRow - 1; i > 0; i--) {

            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, PieceCol); //move to

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void Up(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();

        int k = 0;
        for (int i = PieceRow + 1; i <= BoardWidth; i++) {

            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, PieceCol); //move to

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void Left(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int j = PieceCol - 1; j > 0; j--) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(PieceRow, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void Right(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int j = PieceCol + 1; j <= BoardWidth; j++) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(PieceRow, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void UpRight(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int i = PieceRow + 1, j = PieceCol + 1; i <= BoardHight && j <= BoardWidth; i++, j++) {
            if (k == max) break;
//            System.out.println(i);
//            System.out.println(j);
            ChessPosition newPosition = new ChessPosition(i, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void UpLeft(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int i = PieceRow + 1, j = PieceCol - 1; i <= BoardHight && j > 0; i++, j--) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void DownLeft(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int i = PieceRow - 1, j = PieceCol - 1; i > 0 && j > 0; i--, j--) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            } else {
                break;
            }
            k++;
        }
    }

    public static void DownRight(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();
        int k = 0;

        for (int i = PieceRow - 1, j = PieceCol + 1; i > 0 && j <= BoardWidth; i--, j++) {
            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, j);

            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            }
            else if (board.getPiece(newPosition).getTeamColor() != color) {
                moves.add(new ChessMove(position, newPosition, null));
                break;
            }
            else {
                break;
            }
            k++;
        }
    }

    public static void KnightMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves) {
        int pieceRow = position.getRow();
        int pieceCol = position.getColumn();
        int[][] possibleMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}}; // {row, col}

        for (int[] move : possibleMoves) {
            int newRow = pieceRow + move[0];
            int newCol = pieceCol + move[1];

            ChessPosition newPosition = new ChessPosition(newRow, newCol);

            if (board.isValidPosition(newRow, newCol)) {
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                } else if (pieceAtNewPosition.getTeamColor() != color) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
    }
}
