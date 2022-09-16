package me.hydos.crabday;

import org.opencv.core.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntBinaryOperator;

public class ImageLocator {

    public static void debug(BufferedImage mainImage, List<Rect> location) {
        var f = new JFrame();
        f.getContentPane().add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(mainImage, 0, 0, null);
                for (Rect point : location) {
                    g.setColor(Color.RED);
                    g.drawRect(point.x, point.y, point.width, point.height);
                }
            }
        });

        f.setTitle("Debug Detection Window");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(mainImage.getWidth() + 10, mainImage.getHeight() + 20);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    static List<Point> findImageLocation(BufferedImage mainImage, BufferedImage subImage, int threshold) {
        return findImageLocation(mainImage, subImage, (rgb0, rgb1) -> {
            int difference = computeDifference(rgb0, rgb1);
            if (difference > threshold) {
                return 1;
            }
            return 0;
        });
    }

    private static int computeDifference(int rgb0, int rgb1) {
        if (rgb0 == 0 || rgb1 == 0) {
            return 0;
        }

        var r0 = (rgb0 & 0x00FF0000) >> 16;
        var g0 = (rgb0 & 0x0000FF00) >> 8;
        var b0 = (rgb0 & 0x000000FF);

        var r1 = (rgb1 & 0x00FF0000) >> 16;
        var g1 = (rgb1 & 0x0000FF00) >> 8;
        var b1 = (rgb1 & 0x000000FF);

        var dr = Math.abs(r0 - r1);
        var dg = Math.abs(g0 - g1);
        var db = Math.abs(b0 - b1);

        return dr + dg + db;
    }

    static List<Point> findImageLocation(BufferedImage mainImage, BufferedImage subImage, IntBinaryOperator rgbComparator) {
        var points = new ArrayList<Point>();
        var w = mainImage.getWidth();
        var h = mainImage.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (isSubImageAt(mainImage, x, y, subImage, rgbComparator)) {
                    points.add(new Point(x, y));
                }
            }
        }

        return points;
    }

    static boolean isSubImageAt(BufferedImage mainImage, int x, int y, BufferedImage subImage, IntBinaryOperator rgbComparator) {
        var w = subImage.getWidth();
        var h = subImage.getHeight();
        if (x + w > mainImage.getWidth()) return false;
        if (y + h > mainImage.getHeight()) return false;

        for (int ix = 0; ix < w; ix++) {
            for (int iy = 0; iy < h; iy++) {
                int mainRgb = mainImage.getRGB(x + ix, y + iy);
                int subRgb = subImage.getRGB(ix, iy);
                if (rgbComparator.applyAsInt(mainRgb, subRgb) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}