package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Math.pow;

public class HCView {

    private final CurveImageDisplay displayImage;

    private Integer order = 1;

    public HCView() {
        displayImage = new CurveImageDisplay();
    }

    public void start() {

        JFrame frame = new JFrame("Hilbert Curve");
        JButton btnStart = new JButton("Start");
        JPanel top = new JPanel();
        JLabel label = new JLabel("Order");
        JComboBox<Integer> comboBox = new JComboBox<>();
        frame.setTitle("Hilbert curve");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        top.add(label);
        top.add(comboBox);

        frame.add(top, BorderLayout.NORTH);
        frame.add(displayImage, BorderLayout.CENTER);
        frame.add(btnStart, BorderLayout.SOUTH);

        ButtonEventListener btnListener = new ButtonEventListener();
        for (int i = 1; i < 8; i++)
            comboBox.addItem(i);
        comboBox.addActionListener(e -> {
            if (btnListener.btnPressed) btnStart.doClick();
            setOrder((Integer) comboBox.getSelectedItem());
        });
        btnStart.addActionListener(btnListener);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private class ButtonEventListener implements ActionListener {

        boolean btnPressed = false;

        long threadId;

        @Override
        public void actionPerformed(ActionEvent e) {

            if (!btnPressed) {
                Thread t = new Thread(HCView.this::drawCurve);
                threadId = t.getId();
                t.setDaemon(true);
                t.start();
            } else {
                for (Thread thread : Thread.getAllStackTraces().keySet())
                    if (thread.getId() == threadId) thread.interrupt();
            }
            btnPressed = !btnPressed;
        }

    }

    private void drawCurve() {

        int n = (int) pow(2, order);
        int total = n * n;
        int lineLength = displayImage.getWidth() / n;
        Point[] path = new Point[total];
        for (int i = 0; i < total; i++) {
            path[i] = hilbertPoint(i);
            path[i].x = path[i].x * lineLength + lineLength / 2;
            path[i].y = path[i].y * lineLength + lineLength / 2;
        }
        draw(path);
    }

    private void draw(Point[] points) {

        try {
            while (true) {
                displayImage.clearImage();
                for (int i = 0; i < points.length - 1; i++) {
                    displayImage.drawLine(points[i], points[i + 1]);
                    Thread.sleep(10);
                    displayImage.repaint();
                }
            }
        } catch (InterruptedException e) {
            displayImage.clearImage();
            displayImage.repaint();
        }
    }

    private Point hilbertPoint(int i) {

        Point[] points = {
                new Point(0, 0),
                new Point(0, 1),
                new Point(1, 1),
                new Point(1, 0)
        };
        Point point = points[i & 3];
        for (int j = 1; j < order; j++) {
            i >>>= 2;
            int l = (int) pow(2, j);
            int temp;
            switch (i & 3) {
                case 0:
                    temp = point.x;
                    point.x = point.y;
                    point.y = temp;
                    break;
                case 1:
                    point.y += l;
                    break;
                case 2:
                    point.x += l;
                    point.y += l;
                    break;
                case 3:
                    temp = l - 1 - point.x;
                    point.x = l - 1 - point.y;
                    point.y = temp;
                    point.x += l;
                    break;
            }
        }
        return point;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

}
