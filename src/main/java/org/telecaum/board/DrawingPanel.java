package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
// 그림판 패널
public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {
    private BufferedImage image;
    Point firstPointer = new Point(0, 0);
    Point secondPointer = new Point(0, 0);
    private Color customColor = new Color(0,0,0);
    private Float stroke = (float) 5;
    public ArrayList<Line> lines = new ArrayList<>();
    ArrayList<int[]> temp = new ArrayList<>();
    boolean erase = false;

    public DrawingPanel(){
        setLayout(new BorderLayout());
        setImageBackground();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     *      Save a single of lines through this logic, ArrayList< ArrayList<int[]> >
     *      including color, stroke and isSelected infos
     **/
    public class Line {
        ArrayList<int[]> multipleLine;
        boolean isSelected;
        private Color color = new Color(0,0,0,255);
        private Float stroke = (float) 5;

        public Line(ArrayList<int[]> temp, Color color, Float stroke) {
            this.multipleLine = temp;
            this.color = color;
            this.stroke = stroke;
            isSelected = false;
        }
    }

    /**
     * adjust ratio comparing Participant board's size and Host board's size
     * @param width participant's canvas width
     * @param height participant's canvs height
     * @param points points from Socket.IO participant as ArrayList<int[]> format
     * @return return as ArrayList<int[]> format
     */
    private ArrayList<int[]> conversion(int width, int height, ArrayList<int[]> points){
        Rectangle r = this.getBounds();
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
    public void redrawing(){
        Graphics2D g = image.createGraphics();
        // draw rest of lines
        Iterator<Line> iterator = lines.iterator();
        while (iterator.hasNext()) {
            Line line = iterator.next();
            g.setColor(line.color);
            g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

            if(!line.multipleLine.isEmpty()) {
                int[] startpoint = line.multipleLine.get(0);
                int[] endpoint;
                for (int i = 1; i < line.multipleLine.size(); i++) {
                    endpoint = line.multipleLine.get(i);
                    g.drawLine(startpoint[0], startpoint[1], endpoint[0], endpoint[1]);
                    firstPointer.x = secondPointer.x;
                    firstPointer.y = secondPointer.y;
                }
            }
        }
        repaint();
    }

    public void draw(int id, int width, int height, ArrayList<int[]> points, Color color) {
        int[] firstPointer;
        int[] secondPointer;

        temp = conversion(width, height, points);

        Graphics2D g = getBufferedImage().createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        firstPointer = temp.get(0);


        for (int i = 1; i < points.size(); i++) {
            secondPointer = temp.get(i);
            g.drawLine(firstPointer[0], firstPointer[1], secondPointer[0], secondPointer[1]);
            firstPointer = secondPointer;
        }

        g.dispose();
        repaint();
    }
//    public void draw() {
//        int[] firstPointer;
//        int[] secondPointer;
//
//        Graphics2D g = getBufferedImage().createGraphics();
//        g.setColor(color);
//        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
//        firstPointer = temp.get(0);
//
//
//        for (int i = 1; i < points.size(); i++) {
//            secondPointer = temp.get(i);
//            g.drawLine(firstPointer[0], firstPointer[1], secondPointer[0], secondPointer[1]);
//            firstPointer = secondPointer;
//        }
//
//        g.dispose();
//        repaint();
//    }

    public void setImageBackground() {
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
        this.image = new BufferedImage(res.width, res.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0,0,0,1));
        g.fillRect(0, 0, res.width, res.height);
        g.dispose();
    }

    /**
     * paint at the first time with creating the canvas
     * and repaint() method call this method
     * so that need to paint for every single line
     * @param g the <code>Graphics</code> object to protect
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void eraseAll(){
//        lines.clear();
        Iterator<Line> iter = lines.iterator();
        while(iter.hasNext()){
            Line line = iter.next();
            line.multipleLine.clear();
            iter.remove();
        }
    }

    /**
     *
     * clear the canvas with putting a new Transparent panel
     *
     */
    public void clearCanvas(){
        Graphics2D g = image.createGraphics();
        setImageBackground();
        repaint();
    }

    public void updatePaint() {
        Graphics2D g = image.createGraphics();
        g.setColor(customColor);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
        g.dispose();
        repaint();
    }

    public void changeToErase(boolean erase){
        this.erase = erase;
    }
    public void changeColor(Color customColor){
        this.customColor = customColor;
    }
    public void setStrokeSize(float size){
        this.stroke = size;
    }

    /**
     * Returns true if the given point is inside the given line.
     */
    private boolean isInside(ArrayList<int[]> checkSeletectedLine, Point p) {
        int[] startPoint;
        int[] endPoint;
        double minimum = 0xfffffff;
        if(!checkSeletectedLine.isEmpty()) {
            startPoint = checkSeletectedLine.get(0);
            for (int i = 1; i < checkSeletectedLine.size(); i++) {
                endPoint = checkSeletectedLine.get(i);
                double distnace = pointToLineDistance(startPoint[0],startPoint[1],endPoint[0],endPoint[1],p.x,p.y);
                minimum = Math.min(minimum, distnace);
                System.out.println(minimum);
                startPoint = endPoint;
            }

            if(minimum < 5.0)
                return true;
        }
        return false;
    }

    /**
     * Calculates the distance from a point (x, y) to the line segment defined by (x1, y1) and (x2, y2).
     */
    private double pointToLineDistance(double x1, double y1, double x2, double y2, double x, double y) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq != 0) ? dot / len_sq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!erase) {
            firstPointer.setLocation(0, 0);
            secondPointer.setLocation(0, 0);

            firstPointer.setLocation(e.getX(), e.getY());
            int[] coor = { firstPointer.x, firstPointer.y };
            temp.add(coor);
        }
        else{
            for(int i=0; i<lines.size(); i++){
                Line line = lines.get(i);
                if(isInside(line.multipleLine, e.getPoint()))
                    line.isSelected = true;
            }

            Iterator<Line> iterator2 = lines.iterator();

            while (iterator2.hasNext()) {
                Line line = iterator2.next();

                if (line.isSelected == true) {
                    iterator2.remove();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!erase) {
            if (secondPointer.x != 0 && secondPointer.y != 0) {
                firstPointer.x = secondPointer.x;
                firstPointer.y = secondPointer.y;
            }
            secondPointer.setLocation(e.getX(), e.getY());
            int[] coor = {secondPointer.x, secondPointer.y};
            temp.add(coor);

            updatePaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!erase) {
            secondPointer.setLocation(e.getX(), e.getY());
            int[] coor = { secondPointer.x, secondPointer.y };
            temp.add(coor);
            ArrayList<int[]> line = new ArrayList<>(temp);
            lines.add(new Line(line, customColor, stroke));

            updatePaint();
            temp.clear();
        }
        else{
            clearCanvas();
            redrawing();
        }
    }

    public BufferedImage getBufferedImage() {
        return this.image;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

}
