package me.hydos.crabday;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenCvTools {

    private final Path openCV46Tools;
    private final Path openCV34Tools;

    public OpenCvTools(Path openCV46Tools, Path openCV34Tools) {
        this.openCV46Tools = openCV46Tools.resolve("build/x64/vc15/bin").toAbsolutePath();
        this.openCV34Tools = openCV34Tools.resolve("build/x64/vc12/bin").toAbsolutePath();
        System.load(openCV46Tools.resolve("build/java/x64/opencv_java460.dll").toAbsolutePath().toString());
    }

    /**
     * Runs opencv_annotation from OpenCV 4.6
     */
    public void openCvAnnotation(Path posImgList, Path positiveImages) {
        runTool(openCV46Tools.resolve("opencv_annotation.exe").toString(), "--annotations=" + posImgList.toAbsolutePath(), "--images=" + positiveImages.toAbsolutePath());
    }

    /**
     * Runs opencv_createsamples from OpenCV 3.4
     */
    public void openCvCreateSamples(Path relativeDirectory, Path posImgList, int width, int height, int num) {
        var safeRelativeDirectory = makeSafePath(relativeDirectory);
        var outputFile = posImgList.toAbsolutePath().toString().replace(".txt", ".vec");
        runTool(safeRelativeDirectory, openCV34Tools.resolve("opencv_createsamples.exe").toString(), "-info", safeRelativeDirectory.relativize(posImgList.toAbsolutePath()).toString(), "-w", Integer.toString(width), "-h", Integer.toString(height), "-num", Integer.toString(num), "-vec", outputFile);
    }

    /**
     * Runs opencv_traincascade from OpenCV 3.4
     */
    public void openCvTrainCascade(Path relativeDirectory, Path cascadeDir, Path posImgList, Path negImgList, int width, int height, int stageCount) {
        try {
            var posImgCount = Integer.toString(Files.readString(posImgList).split("\n").length);
            var negImgCount = Integer.toString(Files.readString(negImgList).split("\n").length);
            var safeCascadeDir = makeSafePath(cascadeDir).toString();
            var safePosImgList = makeSafePath(posImgList).toString();
            var safeNegImgList = makeSafePath(negImgList).toString();
            var posImgVectors = safePosImgList.replace(".txt", ".vec");

            runTool(relativeDirectory, openCV34Tools.resolve("opencv_traincascade.exe").toString(), "-data", safeCascadeDir, "-vec", posImgVectors, "-bg", safeNegImgList, "-numPos", posImgCount, "-numNeg", negImgCount, "-w", Integer.toString(width), "-h", Integer.toString(height), "-numStages", Integer.toString(stageCount));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't train cascade.", e);
        }
    }

    private void runTool(String... command) {
        try {
            var process = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();

            while (process.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to run " + command[0], e);
        }
    }

    private void runTool(Path directory, String... command) {
        try {
            var process = new ProcessBuilder(command)
                    .directory(directory.toFile())
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();

            while (process.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to run " + command[0], e);
        }
    }

    public static Path makeSafePath(Path directory) {
        var split = directory.toAbsolutePath().toString().split("\\\\");

        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String part = split[i];

            if ((split.length >= i + 2 && split[i + 1].equals("..")) || part.equals("..")) {
                continue;
            }

            newPath.append(part).append("/");
        }

        return Paths.get(newPath.toString());
    }
}
