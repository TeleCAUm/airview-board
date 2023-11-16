package org.telecaum.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telecaum.board.TransparentBoard;

import java.util.ArrayList;

public class Server {

    private Configuration config;
    private SocketIOServer server;
    private TransparentBoard board;
    public Server(TransparentBoard board) {
        this.board = board;

        config = new Configuration();

        config.setHostname("localhost");
        config.setPort(3333);
        config.setOrigin("*");

        server = new SocketIOServer(config);

        server.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
        server.addDisconnectListener(client -> System.out.println("Client disconnected: " + client.getSessionId()));

        // add some listeners
        server.addEventListener("send", String.class, listenDrawingEvent());


        server.start();
    }

    private DataListener<String> listenDrawingEvent() {
        return (client, message, ackRequest) -> {
            ArrayList<int[]> points = new ArrayList<>();

            JSONObject jo = new JSONObject(message);
            int width = jo.getInt("width");
            int height = jo.getInt("height");
            JSONArray line = jo.getJSONArray("line");

            for (int i = 0; i < line.length(); i++) {
                JSONObject point = line.getJSONObject(i);
                int x = point.getInt("x");
                int y = point.getInt("y");
                int[] coordinates = {x, y};
                points.add(coordinates);
            }

            board.draw(points);

            points.clear(); // maybe this is the problem?
            ackRequest.sendAckData("received and drawed!");
        };
    }


}
