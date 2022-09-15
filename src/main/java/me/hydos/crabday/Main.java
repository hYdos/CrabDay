package me.hydos.crabday;

import me.hydos.crabday.window.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws AWTException, IOException {
        var robot = new Robot();
        var window = Window.getWindowInfo("Hayday1");
        window.moveToFront();

        BufferedImage createScreenCapture = robot.createScreenCapture(new Rectangle(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
        ImageIO.write(createScreenCapture, "png", new File("C:\\Users\\hayde\\Desktop\\Maldium\\positive\\" + System.currentTimeMillis() / 1000 + ".png"));

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
}
