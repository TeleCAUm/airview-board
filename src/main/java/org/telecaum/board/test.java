package org.telecaum.board;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class test {
    private JFrame frame;
    private JPanel drawingPanel;
    private JSlider strokeSlider;

    private int strokeWidth = 1;  // Initial stroke width

    public test() {
        frame = new JFrame("Stroke Slider Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Use the stroke width in your drawing logic
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.black);
                g2d.setStroke(new BasicStroke(strokeWidth));
                g2d.drawLine(50, 50, 250, 250);
            }
        };

        strokeSlider = new JSlider(JSlider.HORIZONTAL, 1, 30, 10);
        strokeSlider.setMajorTickSpacing(6);
        strokeSlider.setPaintTicks(true);
        strokeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                strokeWidth = strokeSlider.getValue();
                drawingPanel.repaint();
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the drawing
                drawingPanel.repaint();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Stroke Width:"));
        controlPanel.add(strokeSlider);
        controlPanel.add(clearButton);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(drawingPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new test();
            }
        });
    }
}
