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

public class Server {

    private Configuration config;
    private SocketIOServer server;
    private TransparentBoard board;
    private DrawingPanel drawing;
    int id = 1;
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
            ArrayList<int[]> line = new ArrayList<>();
            Color color = new Color(0, 0, 0);
            float stroke = (float) 30;

            JSONObject jo = new JSONObject(message);
            int width = jo.getInt("width");
            int height = jo.getInt("height");
            JSONArray joline = jo.getJSONArray("line");
//            JSONArray jsColor = jo.getJSONArray("color");

            for (int i = 0; i < joline.length(); i++) {
                JSONObject point = joline.getJSONObject(i);
                int x = point.getInt("x");
                int y = point.getInt("y");
                int[] coordinates = {x, y};
                line.add(coordinates);
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

            conversion(width, height, line);
//            try{
//                SwingUtilities.invokeAndWait(new Runnable(){
//                    @Override
//                    public void run() {
//                        drawing.draw(line);
//                        System.out.println("success!");
//                    }
//                });
//            }catch(Exception e1){
//                System.out.println("error?");
//            }
            board.draw(line,color,stroke);
            id++;
            board.setData(line, color, stroke, 10);
//            drawing.setLines(line, color, stroke, id);
//            line.clear();
//            board.test();
            if(id==10){
                System.out.println("more than 10 lines");
                board.eraseAll();
            }
            ackRequest.sendAckData("received and drawed!");
        };
    }
}
