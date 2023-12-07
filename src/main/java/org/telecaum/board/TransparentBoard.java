package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

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
    public ArrayList<Line> lines = new ArrayList<>();

    public TransparentBoard() {
        init();
    }

    private void init() {
        Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(res.width, res.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
//        customColor = new Color(0,0,0,255);
//        stroke = (float) 5;

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
        buttonBox.setBounds(50, 50, 1000 , 80);
        JButton pen = new JButton();
        pen.setIcon(new ImageIcon("PenOn.png"));
        Box penBox = new Box(BoxLayout.X_AXIS);
        penBox.add(pen);
        penBox.setBounds(500,500,500,500);
        add(penBox);
        add(buttonBox);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
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

    public void draw(ArrayList<int[]> points,Color color, float stroke) {
        Point firstPointer = new Point(0, 0);
        Point secondPointer = new Point(0, 0);

        Graphics2D g = panel.getBufferedImage().createGraphics();

        g.setColor(color);
        g.setStroke(new BasicStroke(stroke));
        int[] first = points.get(0);
        firstPointer.setLocation(first[0], first[1]);

        for (int i = 1; i < points.size(); i++) {
            int[] second = points.get(i);
            secondPointer.setLocation(second[0], second[1]);
            g.drawLine(firstPointer.x, firstPointer.y, secondPointer.x, secondPointer.y);
            firstPointer.x = secondPointer.x;
            firstPointer.y = secondPointer.y;
        }

        g.dispose();
        repaint();
    }
    public void setData(ArrayList<int[]> points, Color color, float stroke, int id){
        lines.add(new Line(points, color, stroke, id));
    }

    public void eraseAll(){
        Iterator<Line> iter = lines.iterator();
        while(iter.hasNext()){
            Line line = iter.next();
            line.multipleLine.clear();
            iter.remove();
        }
        System.out.println(lines.size());
        panel.clearCanvas();
        repaint();
    }

    public class Line {
        ArrayList<int[]> multipleLine;
        boolean isSelected;
        private Color color = new Color(0,0,0,255);
        private Float stroke = (float) 5;
        private int id = 0;

        public Line(ArrayList<int[]> temp, Color color, Float stroke, int id) {
            this.multipleLine = temp;
            this.color = color;
            this.stroke = stroke;
            isSelected = false;
            this.id = id;
        }
    }

    public void test(){
        panel.clearCanvas();
    }
}
