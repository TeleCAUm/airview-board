package org.telecaum.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telecaum.board.TransparentBoard;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {

    private Configuration config;
    private SocketIOServer server;
    private TransparentBoard board;
    public Server(TransparentBoard board) {
        this.board = board;

        config = new Configuration();

        config.setHostname("127.0.0.1");
        config.setPort(3333);
        config.setOrigin("*");

        server = new SocketIOServer(config);

        server.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
        server.addDisconnectListener(client -> System.out.println("Client disconnected: " + client.getSessionId()));

        // add some listeners
        server.addEventListener("send", String.class, listenConversionDrawingEvent());

        server.start();
    }
    private ArrayList<int[]> conversion(int width, int height, ArrayList<int[]> points){
        Rectangle r = board.getBounds();
        int boardWidth = r.width;
        int boardHeight = r.height;
        int dataWidth = width;
        int dataHeight = height;
        double widthRatio = boardWidth/dataWidth;
        double heightRatio = boardHeight/dataHeight;
        ArrayList<int[]> adjustPoints = new ArrayList<>();

        for(int i=0; i<points.size(); i++){
            int[] coor = points.get(i);
            System.out.println("before : " + coor[0] + " " + coor[1]);
            coor[0] = (int)( coor[0] * widthRatio );
            coor[1] = (int)( coor[1] * heightRatio );
            System.out.println("after : " + coor[0] + " " + coor[1]);

            adjustPoints.add(coor);
        }

        return adjustPoints;
    }

    private DataListener<String> listenConversionDrawingEvent() {
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

            conversion(width, height, points);
            board.draw(points);

            points.removeAll(points); // maybe this is the problem?
            ackRequest.sendAckData("received and drawed!");
        };
    }


}
