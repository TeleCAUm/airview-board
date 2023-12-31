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
import java.util.ArrayList;
import java.util.UUID;

public class Server {

    private Configuration config;
    private SocketIOServer server;
    private TransparentBoard board;
    public Server(TransparentBoard board) {
        this.board = board;
        config = new Configuration();

        config.setHostname("0.0.0.0");
        config.setPort(5555);
        config.setOrigin("*");

        server = new SocketIOServer(config);

        server.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
        server.addDisconnectListener(client -> {
            board.eraseAll(client.getSessionId());
        });

        // add some listeners
        server.addEventListener("send", String.class, listenConversionDrawingEvent());

        server.start();
    }

    /**
     * adjust ratio comparing Participant board's size and Host board's size
     * @param width participant's canvas width
     * @param height participant's canvs height
     * @param points points from Socket.IO participant as ArrayList<int[]> format
     * @return return as ArrayList<int[]> format
     */
    private ArrayList<int[]> conversion(int width, int height, ArrayList<int[]> points){
        Rectangle r = board.getBounds();
        int boardWidth = r.width;
        int boardHeight = r.height;
        int dataWidth = width;
        int dataHeight = height;
        double widthRatio = (double)boardWidth/(double)dataWidth;
        double heightRatio = (double)boardHeight/(double)dataHeight;
        ArrayList<int[]> adjustPoints = new ArrayList<>();

        for(int i=0; i<points.size(); i++){
            int[] coor = points.get(i);
            coor[0] = (int) Math.round(coor[0] * widthRatio);
            coor[1] = (int) Math.round(coor[1] * heightRatio);

            adjustPoints.add(coor);
        }

        return adjustPoints;
    }

    private DataListener<String> listenConversionDrawingEvent() {
        return (client, message, ackRequest) -> {
            ArrayList<int[]> line = new ArrayList<>();
            Color color = new Color(0, 0, 0);

            JSONObject jo = new JSONObject(message);
            int width = jo.getInt("width");
            int height = jo.getInt("height");
            JSONArray joline = jo.getJSONArray("line");
            String jsColor = jo.getString("color");
            float thickness = jo.getFloat("thickness");

            for (int i = 0; i < joline.length(); i++) {
                JSONObject point = joline.getJSONObject(i);
                int x = point.getInt("x");
                int y = point.getInt("y");
                int[] coordinates = {x, y};
                line.add(coordinates);
            }

            color = Color.decode(jsColor);


            conversion(width, height, line);
            board.draw(line, color, thickness);
            board.setData(line, color, thickness, client.getSessionId());
            ackRequest.sendAckData("received and drawed!");
        };
    }
}
