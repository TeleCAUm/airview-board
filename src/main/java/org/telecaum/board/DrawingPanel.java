package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

// 그림판 패널
public class DrawingPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private BufferedImage image;
    public JButton penButton = new JButton("그리기");
    public JButton eraseButton = new JButton("지우기");
    String shapeString = "그리기";

    Point firstPointer = new Point(0, 0);
    Point secondPointer = new Point(0, 0);
    private Color customColor;
    private Float stroke;

    DrawingPanel(){
        customColor = new Color(0,0,0,255);
        stroke = (float) 5;

        penButton.addActionListener(this);
        penButton.setSize(80,80);
        eraseButton.addActionListener(this);
        eraseButton.setSize(80, 80);
        setLayout(new BorderLayout());

        setImageBackground();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setImageBackground() {
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
        this.image = new BufferedImage(res.width, res.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0,0,0,1));
        g.fillRect(0, 0, res.width, res.height);
        g.dispose();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public void updatePaint() {
        Graphics2D g = image.createGraphics();

        switch (shapeString) {
            case ("그리기"):
                g.setColor(customColor);
                g.setStroke(new BasicStroke(stroke));
                g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
                break;

            case ("지우기"):
                setImageBackground();
                shapeString = "지우기";
                break;
            default:
                break;
        }
        g.dispose();
        repaint();
    }

    public void changeColor(Color customColor){
        this.customColor = customColor;
    }
    public void setStrokeSize(float size){
        this.stroke = size;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass().toString().contains("JButton")) {
            shapeString = e.getActionCommand();
            if(e.getActionCommand().toString() == "지우기"){
                setImageBackground();
                repaint();
                shapeString = "그리기";
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        firstPointer.setLocation(0, 0);
        secondPointer.setLocation(0, 0);

        firstPointer.setLocation(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (shapeString != "펜") {
            secondPointer.setLocation(e.getX(), e.getY());
            updatePaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (secondPointer.x != 0 && secondPointer.y != 0) {
            firstPointer.x = secondPointer.x;
            firstPointer.y = secondPointer.y;
        }
        secondPointer.setLocation(e.getX(), e.getY());
        updatePaint();
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
