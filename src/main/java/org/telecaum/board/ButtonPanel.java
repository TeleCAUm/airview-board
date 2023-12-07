package org.telecaum.board;

import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class ButtonPanel implements ActionListener {
    public JButton pen;
    public JButton erase;
    public JLabel setStrokSize;
    public JSlider strokeSlider;
    public JButton eraseAll;
    public JPanel buttonPanel;
    DrawingPanel drawingPanel;
    private Color[] colors;
    private JButton[] colorButtons;
    private ImageIcon penOnImgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("./PenOn.png")));
    private ImageIcon penOffImgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("./PenOff.png")));
    private ImageIcon eraseOnImgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("./EraseOn.png")));
    private ImageIcon eraseOffImgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("./EraseOff.png")));
    private ImageIcon deleteOnImgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("./DeleteOn.png")));

    private Image penOnImg, penOffImg, eraseOnImg, eraseOffImg, deleteOnImg;
    boolean flag = true;
        public ButtonPanel(DrawingPanel drawingPanel) {
            this.drawingPanel = drawingPanel;

            // Initialize and configure the main button panel
            buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // Set up images for buttons
            Image penOnImgIconImage = penOnImgIcon.getImage();
            penOnImg = penOnImgIconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            Image penOffImgIconImage = penOffImgIcon.getImage();
            penOffImg = penOffImgIconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            Image eraseOnImgIconImage = eraseOnImgIcon.getImage();
            eraseOnImg = eraseOnImgIconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            Image eraseOffImgIconImage = eraseOffImgIcon.getImage();
            eraseOffImg = eraseOffImgIconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            Image deleteOnImgIconImage = deleteOnImgIcon.getImage();
            deleteOnImg = deleteOnImgIconImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

            // Initialize buttons
            pen = new JButton(new ImageIcon(penOnImg));
            erase = new JButton(new ImageIcon(eraseOnImg));
            setStrokSize = new JLabel("thickness");
            eraseAll = new JButton(new ImageIcon(deleteOnImg));

            // Initialize color buttons
            colorButtons = new JButton[6];
            colors = new Color[]{
                    new Color(255, 139, 139),
                    new Color(255, 202, 139),
                    new Color(255, 236, 139),
                    new Color(211, 255, 139),
                    new Color(155, 255, 139),
                    new Color(139, 255, 206)
            };

            JPanel colorButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            for (int i = 0; i < colors.length; i++) {
                colorButtons[i] = new JButton();
                colorButtons[i].setBackground(colors[i]);
                colorButtons[i].setOpaque(true);
                colorButtons[i].setBorderPainted(false);
                colorButtons[i].setPreferredSize(new Dimension(30, 30));
                colorButtons[i].addActionListener(this);
                colorButtonsPanel.add(colorButtons[i]);
            }

            // Slider for stroke size
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

            // Add components to the main button panel
            buttonPanel.add(pen);
            buttonPanel.add(erase);
            buttonPanel.add(colorButtonsPanel);
            buttonPanel.add(setStrokSize);
            buttonPanel.add(strokeSlider);
            buttonPanel.add(eraseAll);

            // Set action listeners for buttons
            pen.addActionListener(this);
            erase.addActionListener(this);
            eraseAll.addActionListener(this);
        }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(pen)) {
            Color black = new Color(0,0,0,255);
            pen.setIcon(new ImageIcon(penOffImg));
            erase.setIcon(new ImageIcon(eraseOnImg));
            eraseAll.setIcon(new ImageIcon(deleteOnImg));
            drawingPanel.changeColor(black);
            drawingPanel.changeToErase(false);
        } else if (e.getSource().equals(erase)) {
            pen.setIcon(new ImageIcon(penOnImg));
            erase.setIcon(new ImageIcon(eraseOffImg));
            eraseAll.setIcon(new ImageIcon(deleteOnImg));
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
            pen.setIcon(new ImageIcon(penOnImg));
            erase.setIcon(new ImageIcon(eraseOnImg));
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