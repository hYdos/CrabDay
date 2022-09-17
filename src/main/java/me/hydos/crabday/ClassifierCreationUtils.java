package me.hydos.crabday;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import me.hydos.crabday.window.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassifierCreationUtils implements NativeKeyListener {
    private static final String TARGET_NAME = "wheat";
    private static final Path ROOT = Paths.get("../training/" + TARGET_NAME);
    private static final Path POSITIVE_IMAGES = ROOT.resolve("positives/");
    private static final Path POSITIVE_IMAGE_LIST = ROOT.resolve("positive_images.txt");
    private static final Path NEGATIVE_IMAGES = ROOT.resolve("negatives/");
    private static final Path NEGATIVE_IMAGE_LIST = ROOT.resolve("negatives_images.txt");
    private static final Path CASCADE = ROOT.resolve("cascade/");
    private static final OpenCvTools TOOLS = new OpenCvTools(Paths.get("C:\\opencv"), Paths.get("C:\\Users\\hayde\\Downloads\\opencv"));

    private final Robot robot;
    private final String windowName;

    public static void trainCascade() {
        try {
            Files.deleteIfExists(CASCADE);
            Files.createDirectories(CASCADE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TOOLS.openCvCreateSamples(ROOT, POSITIVE_IMAGE_LIST, 20, 20, 1000);
        TOOLS.openCvTrainCascade(ROOT, CASCADE, POSITIVE_IMAGE_LIST, NEGATIVE_IMAGE_LIST, 20, 20, 10);
    }

    public static void createAnnotationFiles() {
        writeAnnotationFile(NEGATIVE_IMAGE_LIST, NEGATIVE_IMAGES);
        writeAnnotationFile(POSITIVE_IMAGE_LIST, POSITIVE_IMAGES);
        TOOLS.openCvAnnotation(POSITIVE_IMAGE_LIST, POSITIVE_IMAGES);
    }

    public static void captureImages(String windowName) {
        try {
            Files.createDirectories(POSITIVE_IMAGES);
            Files.createDirectories(NEGATIVE_IMAGES);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException | IOException e) {
            throw new RuntimeException("There was a problem registering the native hook", e);
        }

        GlobalScreen.addNativeKeyListener(new ClassifierCreationUtils(windowName));
    }

    public ClassifierCreationUtils(String windowName) {
        try {
            this.robot = new Robot();
            this.windowName = windowName;
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeAnnotationFile(Path imagesListFile, Path imagesDir) {
        try {
            Files.deleteIfExists(imagesListFile);

            try (var reader = Files.newBufferedWriter(imagesListFile)) {
                Files.list(imagesDir).forEach(path -> {
                    try {
                        reader.write(OpenCvTools.makeSafePath(path).toAbsolutePath().toString().replace("\\", "/") + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        NativeKeyListener.super.nativeKeyTyped(nativeEvent);
        var window = Window.getWindowInfo(this.windowName);

        switch (nativeEvent.getKeyChar()) {
            case '[', '-' -> {
                var screenCapture = robot.createScreenCapture(new Rectangle(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
                try (var writer = Files.newOutputStream(NEGATIVE_IMAGES.resolve(System.currentTimeMillis() / 1000 + ".png"))) {
                    ImageIO.write(screenCapture, "png", writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Recorded Negative Image");
            }

            case ']', '+', '=' -> {
                var screenCapture = robot.createScreenCapture(new Rectangle(window.getLeft(), window.getTop(), window.getWidth(), window.getHeight()));
                try (var writer = Files.newOutputStream(POSITIVE_IMAGES.resolve(System.currentTimeMillis() / 1000 + ".png"))) {
                    ImageIO.write(screenCapture, "png", writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Recorded Positive Image");
            }
        }
    }
}
