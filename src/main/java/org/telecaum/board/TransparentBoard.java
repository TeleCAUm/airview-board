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

    public void draw(ArrayList<int[]> points) {
        Point firstPointer = new Point(0, 0);
        Point secondPointer = new Point(0, 0);

        Graphics2D g = panel.getBufferedImage().createGraphics();

        g.setColor(customColor);
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
