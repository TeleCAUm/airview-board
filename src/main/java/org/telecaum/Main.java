package org.telecaum;

import org.telecaum.board.TransparentBoard;
import org.telecaum.server.Server;

public class Main {
    public static void main(String[] args) {
        TransparentBoard board = new TransparentBoard();
        Server server = new Server(board);
    }
}