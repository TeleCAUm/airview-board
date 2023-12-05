package org.telecaum.board;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel implements ActionListener {
    public JButton pen;
    public JButton erase;
    public JButton changeColor;
    public JButton setStrokSize;
    public JSlider strokeSlider;
    public JButton eraseAll;
    public JPanel buttonPanel;
    DrawingPanel drawingPanel;
    private Color[] colors;
    private JButton[] colorButtons;
    boolean flag = true;
    public ButtonPanel(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        buttonPanel = new JPanel(new GridLayout(1, 0));

        pen = new JButton();
        pen.setPreferredSize(new Dimension(40,20));
        erase = new JButton();
        erase.setPreferredSize(new Dimension(40,20));
        changeColor = new JButton();
        changeColor.setPreferredSize(new Dimension(40,20));
        colorButtons = new JButton[6];
        colors = new Color[]{
                new Color(255, 139, 139),
                new Color(255, 202, 139),
                new Color(255, 236, 139),
                new Color(211, 255, 139),
                new Color(155, 255, 139),
                new Color(139, 255, 206)
        };
        strokeSlider = new JSlider(JSlider.HORIZONTAL, 5, 30, 5);
        strokeSlider.setMajorTickSpacing(5);
        strokeSlider.setPaintTicks(true);
        strokeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                drawingPanel.setStrokeSize(strokeSlider.getValue());
                drawingPanel.repaint();
            }
        });
        setStrokSize = new JButton();
        setStrokSize.setPreferredSize(new Dimension(20,20));
        eraseAll = new JButton();
        eraseAll.setPreferredSize(new Dimension(20,20));


        pen.addActionListener(this);
        erase.addActionListener(this);
        changeColor.addActionListener(this);
        for (int i = 0; i < colors.length; i++) {
            final int index = i;
            colorButtons[i] = new JButton();
            colorButtons[i].setBackground(colors[i]);
            colorButtons[i].setPreferredSize(new Dimension(30, 30));
            colorButtons[i].setFocusPainted(false);
            changeColor.add(colorButtons[i]);
            colorButtons[i].addActionListener(this);
        }
        setStrokSize.addActionListener(this);
        eraseAll.addActionListener(this);

        Box colors = Box.createHorizontalBox();
        colors.setSize(200,100);
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[0]);
        colors.add(Box.createHorizontalGlue());
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[1]);
        colors.add(Box.createHorizontalGlue());
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[2]);
        colors.add(Box.createHorizontalGlue());
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[3]);
        colors.add(Box.createHorizontalGlue());
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[4]);
        colors.add(Box.createHorizontalGlue());
        colors.add(Box.createHorizontalGlue());
        colors.add(colorButtons[5]);
        colors.add(Box.createHorizontalGlue());

        Box b2 = Box.createHorizontalBox();
        b2.add(Box.createHorizontalGlue());
        b2.add(pen);
        b2.add(Box.createHorizontalGlue());
        Box b3 = Box.createHorizontalBox();
        b3.add(Box.createHorizontalGlue());
        b3.add(erase);
        b3.add(Box.createHorizontalGlue());
        Box b4 = Box.createHorizontalBox();
        b4.add(Box.createHorizontalGlue());
        b4.add(colors);
        b4.add(Box.createHorizontalGlue());
        Box b5 = Box.createHorizontalBox();
        b5.add(Box.createHorizontalGlue());
        b5.add(strokeSlider);
        b5.add(Box.createHorizontalGlue());
        Box b6 = Box.createHorizontalBox();
        b6.add(Box.createHorizontalGlue());
        b6.add(eraseAll);
        b6.add(Box.createHorizontalGlue());

        buttonPanel.add(b2);
        buttonPanel.add(b3);
        buttonPanel.add(b4);
        buttonPanel.add(b5);
        buttonPanel.add(b6);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(pen)) {
            Color black = new Color(0,0,0,255);
            drawingPanel.changeColor(black);
            drawingPanel.changeToErase(false);
        } else if (e.getSource().equals(erase)) {
            drawingPanel.changeToErase(true);
        } else if (e.getSource().equals(setStrokSize)) {
            if(!flag){
                drawingPanel.setStrokeSize((float) 5);
                drawingPanel.changeToErase(false);
                flag = true;
            }
            else{
                drawingPanel.changeToErase(false);
                drawingPanel.setStrokeSize((float) 30);
                flag = false;
            }
        } else if (e.getSource().equals(eraseAll)) {
            drawingPanel.eraseAll();
            drawingPanel.setImageBackground();
            drawingPanel.repaint();
            drawingPanel.changeToErase(true);
        }else{
            for(int i=0; i < colorButtons.length; i++){
                if(e.getSource().equals(colorButtons[i])){
                    drawingPanel.changeColor(colors[i]);
                    drawingPanel.changeToErase(false);
                }
            }
        }
    }
}