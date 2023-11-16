package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TransparentBoard extends JFrame {
    private boolean isVisible = true;
    private DrawingPanel panel;
    private Box buttonBox;
    private Box toolBox;

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

        setUndecorated(true);       // 프레임 투명도 설정
        setBackground(new Color(0, 0, 0, 0));

        panel = new DrawingPanel();
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

        buttonBox = new Box(BoxLayout.Y_AXIS);
        Box b1 = Box.createHorizontalBox();
        b1.add(Box.createHorizontalGlue());
        b1.add(panel.penButton);
        b1.add(Box.createHorizontalGlue());

        Box b2 = Box.createHorizontalBox();
        b2.add(Box.createHorizontalGlue());
        b2.add(panel.eraseButton);
        b2.add(Box.createHorizontalGlue());

        toolBox = new Box(BoxLayout.Y_AXIS);
        toolBox.add(b1);
        toolBox.add(b2);

        buttonBox.add(Box.createVerticalStrut(250));
        buttonBox.add(toggleButton);
        buttonBox.add(toolBox);

        add(buttonBox, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void draw(ArrayList<int[]> points) {
        Point firstPointer = new Point(0, 0);
        Point secondPointer = new Point(0, 0);
        Float stroke = (float) 5;

        Graphics2D g = panel.getBufferedImage().createGraphics();

        g.setColor(Color.black);
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

    private void panelToggling() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            // Minimize the window on Mac
            setExtendedState(JFrame.ICONIFIED);
        } else {
            if (isVisible) {
                toggleButton.setIcon(new ImageIcon(offImg));
                remove(panel);
                buttonBox.remove(toolBox);
            } else {
                toggleButton.setIcon(new ImageIcon(onImg));
                add(panel, BorderLayout.CENTER);
                buttonBox.add(toolBox);
            }
            isVisible = !isVisible;
            revalidate();
            repaint();
        }
    }
}
