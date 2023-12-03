package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private ArrayList<Point> line;

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

//        buttonBox.add(Box.createVerticalStrut(200));  // set the verticalstrut as 200
        buttonBox.add(toggleButton);
        buttonBox.add(buttonpanel.buttonPanel);

//        add(buttonBox, BorderLayout.NORTH);
        buttonBox.setBounds((res.width/2)-200, 50, 500 , 50);
        add(buttonBox);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private ArrayList<Point> conversion(int width, int height, ArrayList<int[]> points){
        Rectangle r = this.getBounds();
        int boardWidth = r.width;
        int boardHeight = r.height;
        int dataWidth = width;
        int dataHeight = height;
        double widthRatio = boardWidth/dataWidth;
        double heightRatio = boardHeight/dataHeight;
        ArrayList<Point> adjustPoints = new ArrayList<>();
        Point adjustPoint;

        for(int i=0; i<points.size(); i++){
            int[] coor = points.get(i);
            coor[0] = (int)( coor[0] * widthRatio );
            coor[1] = (int)( coor[1] * heightRatio );
            adjustPoint = new Point(coor[0], coor[1]);

            adjustPoints.add(adjustPoint);
        }

        return adjustPoints;
    }

    public void draw(int width, int height, ArrayList<int[]> points) {
        Point firstPointer = new Point(0, 0);
        Point secondPointer = new Point(0, 0);

        line = conversion(width, height, points);      // adjust ratio comparing Participant board's size and Host board's size

        Graphics2D g = panel.getBufferedImage().createGraphics();
        g.setColor(customColor);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        firstPointer = line.get(0);


        for (int i = 1; i < points.size(); i++) {
            secondPointer = line.get(i);
            g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
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
