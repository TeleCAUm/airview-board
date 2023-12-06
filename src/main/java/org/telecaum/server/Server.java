package org.telecaum.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telecaum.board.DrawingPanel;
import org.telecaum.board.TransparentBoard;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class Server {

    private Configuration config;
    private SocketIOServer server;
    private TransparentBoard board;
    private DrawingPanel drawing;
    public Server(TransparentBoard board, DrawingPanel drawing) {
        this.board = board;
        this.drawing = drawing;

        config = new Configuration();

        config.setHostname("127.0.0.1");
        config.setPort(5555);
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
            coor[0] = (int)( coor[0] * widthRatio );
            coor[1] = (int)( coor[1] * heightRatio );

            adjustPoints.add(coor);
        }

        return adjustPoints;
    }

    private DataListener<String> listenConversionDrawingEvent() {
        return (client, message, ackRequest) -> {
            ArrayList<int[]> points = new ArrayList<>();
            Color color = new Color(0,0,0);

            JSONObject jo = new JSONObject(message);
            int width = jo.getInt("width");
            int height = jo.getInt("height");
            JSONArray line = jo.getJSONArray("line");
//            JSONArray jsColor = jo.getJSONArray("color");

            for (int i = 0; i < line.length(); i++) {
                JSONObject point = line.getJSONObject(i);
                int x = point.getInt("x");
                int y = point.getInt("y");
                int[] coordinates = {x, y};
                points.add(coordinates);
            }

//            for(int i = 0; i < jsColor.length(); i++){
//                JSONObject hexColor = jsColor.getJSONObject(i);
//                String hexWithHash = hexColor.getString("");
//                String numberOnly = hexWithHash.replaceAll("[^0-9]","");
//                int RGBA = Integer.parseInt(numberOnly);
//                int R = RGBA/10000;
//                int G = RGBA/100;
//                int B = RGBA%100;
//                color = new Color(R, G, B);
//            }
            conversion(width, height, points);
            board.draw(points);

            points.clear();
            ackRequest.sendAckData("received and drawed!");
        };
    }
}
