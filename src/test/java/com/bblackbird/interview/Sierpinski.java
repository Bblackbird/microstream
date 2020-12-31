package com.bblackbird.interview;

import inputOutput.StdDraw;

public class Sierpinski {

    public static double height(double length) {

        double h = (length * Math.sqrt(3)) / 2.0;

        return h;
    }


    public static void filledTriangle(double x, double y, double length) {

        double h = height(length);

        StdDraw.filledPolygon(new double[]{x, x - (length / 2.0), x + (length / 2.0)}, new double[]{y, y + h, y + h});

    }


    public static void sierpinski(int n, double x, double y, double length) {

        if (n > 0) {

            filledTriangle(x, y, length);

            sierpinski(n - 1, x, y + height(length), length / 2);

            sierpinski(n - 1, x - (length / 2.0), y, length / 2);

            sierpinski(n - 1, x + (length / 2.0), y, length / 2);

        }

    }

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);

        double length = 1;

        double h = height(length);

        StdDraw.polygon(new double[]{0, length / 2, length}, new double[]{0, h, 0});

        sierpinski(n, length / 2, 0, length / 2);

    }
}
