package me.hydos.crabday;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws AWTException, IOException {
        var robot = new Robot();
        var window = getWindowInfo("Hayday1");
        var windowLeft = (int) (window.rect.left * 0.572);
        var windowTop = (int) (window.rect.top * 0.572);
        window.moveToFront();

        BufferedImage createScreenCapture = robot.createScreenCapture(new Rectangle(windowLeft, windowTop, (int) ((window.rect.right - window.rect.left) / 1.74), (int) ((window.rect.bottom - window.rect.top) / 1.68)));
        ImageIO.write(createScreenCapture, "png", new File("screen.png"));

        var subImage = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/wheat.png"), "Couldn't find Wheat Image"));
        var locations = ImageLocator.findImageLocation(createScreenCapture, subImage, 88);
        System.out.println("Estimated around " + locations.size() + " wheat locations");

        if (locations.size() > 0) {
            // Get out harvesting tool, so we can just drag over the rest
            var sickleOffsetX = 40;
            var sickleOffsetY = 20;
            var firstLocation = locations.get(0);
            robot.mouseMove(windowLeft + firstLocation.x, windowTop + firstLocation.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseMove(windowLeft + firstLocation.x - sickleOffsetX, windowTop + firstLocation.y - sickleOffsetY);
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
                    robot.mouseMove(windowLeft + location.x + xJitter, windowTop + location.y + yJitter);
                    Thread.sleep(5);
                    lastLocation = location;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            //SwingUtilities.invokeLater(() -> ImageLocator.debug(createScreenCapture, subImage, locations));
        }
    }

    public static Window getWindowInfo(String titleName) {
        var hWnd = User32.INSTANCE.FindWindow(null, titleName);
        var r = new WinDef.RECT();
        var buf = new char[1024];
        User32.INSTANCE.GetWindowRect(hWnd, r);
        User32.INSTANCE.GetWindowText(hWnd, buf, buf.length);
        return new Window(hWnd, r, Native.toString(buf));
    }

    public record Window(WinDef.HWND hwnd, WinDef.RECT rect, String title) {

        public void moveToFront() {
            User32.INSTANCE.SetForegroundWindow(hwnd);
        }
    }
}
