package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PrintBoard {
    private static final int BOARD_SIZE = 8;
    private enum Color {WHITE, BLACK}
    private ChessPosition currentPosition;
    private final ChessGame game;
    private String gamecolor;

    public PrintBoard(ChessGame game, String color) {
        this.game = game;
        this.gamecolor = color;
    }

    public void printBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        out.print("\n\n");
        if(gamecolor.toUpperCase().equals("WHITE")) {
            printBoardParts(out, Color.WHITE); // Light orientation
            out.print("\n\n");
        }
        else {
            printBoardParts(out, Color.BLACK);  // Dark orientation
            out.print("\n\n");
        }
    }

    private void printBoardParts(PrintStream out, Color orientation) {
        currentPosition = (orientation == Color.WHITE) ? new ChessPosition(8, 1) : new ChessPosition(1, 8);
        printHeaders(out, orientation);
        for (int row = 0; row < BOARD_SIZE; row++) {
            printRow(out, orientation, row);
            out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            out.printf(" %d ", orientation == Color.WHITE ? 8 - row : row + 1);
            out.print(EscapeSequences.RESET_BG_COLOR);
            moveRow(orientation);
            if (row < BOARD_SIZE - 1) {out.print("\n");}
        }
        out.print(EscapeSequences.RESET_BG_COLOR);
    }
    private void printRow(PrintStream out, Color orientation, int row) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            boolean isLightSquare = (row + col) % 2 == 0;
            squareColor(out, isLightSquare);
            printPiece(out);
            updateColumn(orientation);
        }
        moveColumn(orientation);
    }
    private void printHeaders(PrintStream out, Color orientation) {
        resetWhite(out);
        String[] labels = (orientation == Color.WHITE) ?
                new String[]{"a ", "b ", "c", "d ", "e", "f ", "g", "h"} :
                new String[]{"h ", "g ", "f", "e ", "d", "c ", "b" , "a" };
        for (String label : labels) {
            out.printf(" %s ", label);
        }
        out.print(EscapeSequences.RESET_BG_COLOR + "\n");
    }
    private void printPiece(PrintStream out) {
        ChessPiece piece = game.getBoard().getPiece(currentPosition);
        if (piece != null) {
            pieceColor(out, piece.getTeamColor());
            out.print(piecePrint(piece.getPieceType(), piece.getTeamColor()));
        } else {
            out.print(EscapeSequences.EMPTY);
        }
    }
    private void squareColor(PrintStream out, boolean isLightSquare) {
        if (isLightSquare) {
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        } else {
            out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void pieceColor(PrintStream out, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        } else {
            out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
        }

    }
    private String piecePrint(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        return switch (type) {
            case PAWN -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case ROOK -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING -> color == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
        };
    }
    private void updateColumn(Color orientation) {
        if (orientation == Color.WHITE) {
            currentPosition.setCol(currentPosition.getColumn() + 1);
        } else {
            currentPosition.setCol(currentPosition.getColumn() - 1);
        }
    }
    private void moveColumn(Color orientation) {
        currentPosition.setCol(orientation == Color.WHITE ? 1 : 8);
    }
    private void moveRow(Color orientation) {
        if (orientation == Color.WHITE) {
            currentPosition.setRow(currentPosition.getRow() - 1);
        } else {
            currentPosition.setRow(currentPosition.getRow() + 1);
        }
    }
    private void resetWhite(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        out.print(EscapeSequences.SET_TEXT_BOLD);
    }
}

