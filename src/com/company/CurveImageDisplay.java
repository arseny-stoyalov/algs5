package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CurveImageDisplay extends JComponent {

    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 512;

    private final BufferedImage image;

    public CurveImageDisplay(int width, int height) {

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, width, height);
        setPreferredSize(new Dimension(width, height));
    }

    public CurveImageDisplay() {

        this.image =
                new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    public void drawPixel(Point p, int color) {
        image.setRGB(p.x, p.y, color);
    }

    public void clearImage(){
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    public void drawLine(Point first, Point second){
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setPaint(Color.BLACK);
        g.drawLine(first.x, first.y, second.x, second.y);
    }

}
