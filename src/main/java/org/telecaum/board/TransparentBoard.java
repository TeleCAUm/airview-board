package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class TransparentBoard extends JFrame {
    private boolean isVisible = true;
    private DrawingPanel panel;
    private ButtonPanel buttonpanel;
    private Box buttonBox;
    private Color customColor;
    private Float stroke;
    private ImageIcon onImgIcon = new ImageIcon("./img/on.png");
    private ImageIcon offImgIcon = new ImageIcon("./img/off.png");
    private Image onImg;
    private Image offImg;
    private JButton toggleButton;
    private ArrayList<int[]> line;

    public TransparentBoard() {
        init();
    }

    private void init() {
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(res.width, res.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        customColor = new Color(0,0,0,255);
        stroke = (float) 5;

        setUndecorated(true);       // 프레임 투명도 설정
        setBackground(new Color(0, 0, 0, 0));

        panel = new DrawingPanel();
        buttonpanel = new ButtonPanel(panel);
        panel.setOpaque(false);  // 패널 투명도 설정

        Image onOriginal = onImgIcon.getImage();
        onImg = onOriginal.getScaledInstance(40, 20, Image.SCALE_SMOOTH);
        Image offOriginal = offImgIcon.getImage();
        offImg = offOriginal.getScaledInstance(40, 20, Image.SCALE_SMOOTH);

        toggleButton = new JButton(new ImageIcon(onImg));
        toggleButton.setPreferredSize(new Dimension(40, 20));
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelToggling();
            }
        });
        buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(toggleButton);
        buttonBox.add(buttonpanel.buttonPanel);
        buttonBox.setBounds(100, 50, 1000 , 200);
        add(buttonBox);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
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

    public void draw(int width, int height, ArrayList<int[]> points) {
        int[] firstPointer;
        int[] secondPointer;

        line = conversion(width, height, points);

        Graphics2D g = panel.getBufferedImage().createGraphics();
        g.setColor(customColor);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        firstPointer = line.get(0);


        for (int i = 1; i < points.size(); i++) {
            secondPointer = line.get(i);
            g.drawLine(firstPointer[0], firstPointer[1], secondPointer[0], secondPointer[1]);
            firstPointer = secondPointer;
        }

        g.dispose();
        repaint();
    }

    public void panelToggling() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            // Minimize the window on Mac
            setExtendedState(JFrame.ICONIFIED);
        } else {
            if (isVisible) {
                toggleButton.setIcon(new ImageIcon(offImg));
                remove(panel);
                buttonBox.remove(buttonpanel.buttonPanel);
            } else {
                toggleButton.setIcon(new ImageIcon(onImg));
                add(panel, BorderLayout.CENTER);
                buttonBox.add(buttonpanel.buttonPanel);
            }
            isVisible = !isVisible;
            revalidate();
            repaint();
        }
    }
}
