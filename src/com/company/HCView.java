package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

public class HCView {

    private final CurveImageDisplay displayImage;

    private Integer order = 7;

    public HCView() {
        displayImage = new CurveImageDisplay();
    }

    public void start() {

        JFrame frame = new JFrame("Hilbert's Curve");
        JButton btnStart = new JButton("Start/Stop");
        JPanel top = new JPanel();
        JLabel label = new JLabel("Order:");
        JComboBox<Integer> comboBox = new JComboBox<>();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        top.add(label);
        top.add(comboBox);

        frame.add(top, BorderLayout.NORTH);
        frame.add(displayImage, BorderLayout.CENTER);
        frame.add(btnStart, BorderLayout.SOUTH);

        ButtonEventListener btnListener = new ButtonEventListener();
        for (int i = 7; i > 0; i--)
            comboBox.addItem(i);
        comboBox.addActionListener(e -> {
            if (btnListener.btnPressed) {
                btnStart.doClick();
                displayImage.clearImage();
            }
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
        List<Point> points = recursiveHilbert(order);
        for (Point point : points) {
            point.x = point.x * lineLength + lineLength / 2;
            point.y = point.y * lineLength + lineLength / 2;
        }
        connectPoints(points);
    }

    private void connectPoints(List<Point> points) {

        try {
            while (true) {
                displayImage.clearImage();
                for (int i = 0; i < points.size() - 1; i++) {
                    displayImage.drawLine(points.get(i), points.get(i + 1));
                    Thread.sleep(10);
                    displayImage.repaint();
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    private List<Point> recursiveHilbert(int order) {

        List<Point> list = new ArrayList<>(4);
        if (order == 1) {
            list.add(new Point(0, 0));
            list.add(new Point(0, 1));
            list.add(new Point(1, 1));
            list.add(new Point(1, 0));
            return list;
        }
        int length = (int) pow(2, order - 1);
        List<Point> part = recursiveHilbert(order - 1);

        list.addAll(part.stream().map(point ->
                new Point(point.y, point.x))
                .collect(Collectors.toList()));

        list.addAll(part.stream().map(point ->
                new Point(point.x, point.y + length))
                .collect(Collectors.toList()));

        list.addAll(part.stream().map(point ->
                new Point(point.x + length, point.y + length))
                .collect(Collectors.toList()));

        list.addAll(part.stream().map(point -> {
            int x = length - 1 - point.y;
            int y = length - 1 - point.x;
            return new Point(x + length, y);
        }).collect(Collectors.toList()));

        return list;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

}
