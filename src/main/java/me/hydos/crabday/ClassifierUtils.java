package me.hydos.crabday;

import me.hydos.crabday.window.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassifierUtils {

    public static void main(String[] args) {

    }

    public static void saveImage(boolean positive) throws AWTException, IOException {
        var robot = new Robot();
        var window = Window.getWindowInfo("Hayday1");
        window.moveToFront();

        BufferedImage createScreenCapture = robot.createScreenCapture(new Rectangle(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
        ImageIO.write(createScreenCapture, "png", new File("C:\\Users\\hayde\\Desktop\\Maldium\\positive\\" + System.currentTimeMillis() / 1000 + ".png"));
    }

    public static void createAnnotationFiles() throws IOException {
        Path posPath = Paths.get("C:\\Users\\hayde\\Desktop\\Maldium\\positive.txt");
        Path negPath = Paths.get("C:\\Users\\hayde\\Desktop\\Maldium\\negative.txt");

        try (var reader = Files.newBufferedWriter(negPath)) {
            Files.list(Paths.get("C:\\Users\\hayde\\Desktop\\Maldium\\negative")).forEach(path -> {
                try {
                    reader.write(path.toAbsolutePath() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        try (var reader = Files.newBufferedWriter(posPath)) {
            Files.list(Paths.get("C:\\Users\\hayde\\Desktop\\Maldium\\positive")).forEach(path -> {
                try {
                    reader.write(path.toAbsolutePath() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
