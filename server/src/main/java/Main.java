import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            DataAccess dataAccess = new MemoryDataAccess();

//            if (args.length >= 2 && args[1].equals("sql")) { //add later
//               dataAccess = new MySqlDataAccess();
//            }

//            var service = new ChessService(dataAcess);
//            var server = new Server(service).run(port);
            var server = new Server().run(port);
            port = server;

            System.out.printf("Server started on port %d with %s%n", port, dataAccess.getClass());

            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
            return;
        }
        catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
            Pet Server:
            java ServerMain <port> [sql]
            """);
    }
}