package org.telecaum.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel implements ActionListener {
    public JButton pen;
    public JButton erase;
    public JButton changeColor;
    public JButton setStrokSize;
    public JButton eraseAll;
    public JPanel buttonPanel;
    DrawingPanel drawingPanel;
    boolean flag = true;
    public ButtonPanel(DrawingPanel drawingPanel){
        this.drawingPanel = drawingPanel;
        buttonPanel = new JPanel(new GridLayout(1,0));

        pen = new JButton("1");
        pen.setPreferredSize(new Dimension(40,20));
        erase = new JButton("erase");
        erase.setPreferredSize(new Dimension(40,20));
        changeColor = new JButton("changeColor");
        changeColor.setPreferredSize(new Dimension(40,20));
        setStrokSize = new JButton("setStrokeSize");
        setStrokSize.setPreferredSize(new Dimension(40,20));
        eraseAll = new JButton("eraseAll");
        eraseAll.setPreferredSize(new Dimension(40,20));

        pen.addActionListener(this);
        erase.addActionListener(this);
        changeColor.addActionListener(this);
        setStrokSize.addActionListener(this);
        eraseAll.addActionListener(this);

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
        b4.add(changeColor);
        b4.add(Box.createHorizontalGlue());
        Box b5 = Box.createHorizontalBox();
        b5.add(Box.createHorizontalGlue());
        b5.add(setStrokSize);
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
        } else if (e.getSource().equals(changeColor)) {
            Color chageColor = new Color(255,0,0,255);
            drawingPanel.changeColor(chageColor);
            drawingPanel.changeToErase(false);
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
        }
    }
}