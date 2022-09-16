package me.hydos.crabday;

import me.hydos.crabday.window.Window;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws AWTException, IOException {
        var robot = new Robot();
        var window = Window.getWindowInfo("Hayday1");
        window.moveToFront();

        System.load("C:/opencv/build/java/x64/opencv_java460.dll");
        var screenCapture = robot.createScreenCapture(new Rectangle(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
        var src = load(screenCapture);
        var pDetections = new MatOfRect();
        var classifier = new CascadeClassifier("E:/Projects/hYdos/CrabDay/training/Wheat/training/cascade.xml");
        classifier.detectMultiScale(src, pDetections);
        var detections = pDetections.toList();
        SwingUtilities.invokeLater(() -> ImageLocator.debug(screenCapture, detections));
        System.out.println("e");

        //var subImage = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/wheat.png"), "Couldn't find Wheat Image"));
/*        var locations = ImageLocator.findImageLocation(createScreenCapture, subImage, 88);
        System.out.println("Estimated around " + locations.size() + " wheat locations");

        if (locations.size() > 0) {
            // Get out harvesting tool, so we can just drag over the rest
            var sickleOffsetX = 40;
            var sickleOffsetY = 20;
            var firstLocation = locations.get(0);
            robot.mouseMove(window.getLeft() + firstLocation.x, window.getTop() + firstLocation.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove(window.getLeft() + firstLocation.x - sickleOffsetX, window.getTop() + firstLocation.y - sickleOffsetY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // There is a chance we might miss some wheat with our detection, Get the outer ranges, by using the last position so we can hit the bits we may miss
            Point lastLocation = null;
            for (Point location : locations) {
                var xJitter = 0;
                var yJitter = 0;

                if (lastLocation != null) {
                    xJitter = (int) ((lastLocation.x - location.x) * 1.5);
                    yJitter = (int) ((lastLocation.y - location.y) * 1.5);
                }

                try {
                    robot.mouseMove(window.getLeft() + location.x + xJitter, window.getTop() + location.y + yJitter);
                    Thread.sleep(5);
                    lastLocation = location;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            SwingUtilities.invokeLater(() -> ImageLocator.debug(createScreenCapture, subImage, locations));
        }*/
    }

    public static Mat load(BufferedImage image) throws IOException {
        Path path = Paths.get("pain.png");
        ImageIO.write(image, "png", new FileImageOutputStream(path.toFile()));
        return Imgcodecs.imread(path.toAbsolutePath().toString());
    }
}
