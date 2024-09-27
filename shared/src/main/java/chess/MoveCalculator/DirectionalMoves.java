package chess.MoveCalculator;

import chess.*;
import java.util.HashSet;

public class DirectionalMoves {
    private static final int BoardHight = 8;
    private static final int BoardWidth = 8;


    public static void Down(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves, int max) {
        int PieceRow = position.getRow();
        int PieceCol = position.getColumn();

        int k = 0;
        for (int i = PieceRow - 1; i > 0; i--) {

            if (k == max) break;

            ChessPosition newPosition = new ChessPosition(i, PieceCol); //move to

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

    public static void PawnMove(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, HashSet<ChessMove> moves) {
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


}
