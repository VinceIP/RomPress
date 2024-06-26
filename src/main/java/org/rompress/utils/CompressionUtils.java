package org.rompress.utils;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.rompress.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
RomPress version 0.1
Authors:
    @Vince Patterson
*/

public class CompressionUtils {
    public final static String outputDir = "output";
    public static Path outputPath = null;

    //Scans for and filters relevant files (zip and rar) and returns them as a List of Paths
    public static List<Path> scanArchives(String inputDir) throws IOException, InvalidArgumentException {
        //Convert inputDir String to a Path
        Path workingPath = Paths.get(inputDir);
        //Throw error if Path is not valid
        if (!Files.exists(workingPath) || !Files.isDirectory(workingPath)) {
            throw new InvalidArgumentException("ERROR: The directory '" + inputDir + "' is invalid or does not exist.");
        }
        System.out.println("Pressing directory: " + "'" + inputDir + "'");
        try (Stream<Path> fileStream = Files.list(workingPath)) {
            return fileStream.filter(p -> {
                String fileName = p.getFileName().toString();
                return fileName.endsWith(".zip") || fileName.endsWith(".rar");
            }).collect(Collectors.toList());
        }
    }

    //Scans ROM files and returns a list of Paths to them. targetDir should be the output created by extractFiles
    //TBD: Look for specific ROM files? .nes, .sfc, etc? This is ok, for now
    public static List<Path> scanRoms(String targetDir) throws IOException {
        //Path workingPath = Paths.get(targetDir);
        Path workingPath = outputPath;
        if (!Files.exists(workingPath)) {
            throw new RuntimeException("ERROR: The directory '" + targetDir + "' is invalid or does not exist.");
        }
        System.out.println("Preparing directory '" + workingPath.toString() + "' for processing.");
        try (Stream<Path> fileStream = Files.list(workingPath)) {
            return fileStream.filter(p -> {
                String fileName = p.getFileName().toString();
                return fileName.endsWith(".nes") || fileName.endsWith(".sfc") || fileName.endsWith(".z64") || fileName.endsWith(".gba");
            }).collect(Collectors.toList());

        }
    }

    //Get the current size of relevant files in a directory
    public static long calculateDirectorySize(Path path) throws IOException {
        //Try to walk through our files
        try (Stream<Path> stream = Files.walk(path)) {
            //Find files that are not symbolic links/directories, map their file size to a LongStream, and sum the value
            // of that stream
            return stream.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).sum();


        }
    }


    //Extract files and write them to an output path
    public static void extractFiles(Path inputPath, List<Path> workingFiles) throws IOException, InterruptedException {
        //Create an output directory for extracted files if it doesn't exist
        outputPath = inputPath.resolve(outputDir);
        if (!Files.exists(outputPath)) Files.createDirectory(outputPath);
        //Wrap process in a progress bar

        try (ProgressBar progressBarExtraction = new ProgressBarBuilder().setInitialMax(workingFiles.size()).setStyle(ProgressBarStyle.ASCII).setTaskName("Extracting files... ").setUpdateIntervalMillis(20).build();) {
            for (Path file : workingFiles) {
                progressBarExtraction.setExtraMessage(" - " + file.getFileName().toString());
                extractFile(file, outputPath);
                progressBarExtraction.step();

            }
        }

    }


    private static int extractFile(Path archive, Path outputPath) throws IOException, InterruptedException {
        String workingDir = System.getProperty("user.dir");
        Path sevenZipPath = Paths.get(workingDir, "7zip", "7z.exe");
        if (!Files.exists(sevenZipPath)) {
            throw new IOException("7z not found at " + sevenZipPath);
        }

        if (!Files.exists(archive)) {
            throw new IOException("Archive not found at " + archive);
        }

        if (!Files.isDirectory(outputPath)) {
            throw new IOException("Output path is not a directory: " + outputPath);
        }
        //Create a Process, setup command for 7zip
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = Arrays.asList(sevenZipPath.toString(), "x", archive.toAbsolutePath().toString(), "-o" + outputPath.toAbsolutePath());
        processBuilder.command(command);
        processBuilder.directory(outputPath.toFile());
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("7zip process exited with code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("7zip process was interrupted", e);
        }
        return 0;
    }

    public static void compressFilesAs7z(List<Path> workingFiles) throws IOException {
        try (ProgressBar progressBar = new ProgressBarBuilder().setInitialMax(workingFiles.size()).setStyle(ProgressBarStyle.ASCII).setTaskName("Compressing files... ").setUpdateIntervalMillis(20).build();) {
            for (Path file : workingFiles) {
                progressBar.setExtraMessage(" - " + file.getFileName().toString());
                compressFileAs7z(file);
                progressBar.step();
            }
        }

    }

    public static void compressFileAs7z(Path file) throws IOException {
        //Setup 7z
        String workingDir = System.getProperty("user.dir");
        Path sevenZipPath = Paths.get(workingDir, "7zip", "7z.exe");
        if (!Files.exists(sevenZipPath)) {
            throw new IOException("7z not found at " + sevenZipPath);
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = getCommand(file, sevenZipPath);
        processBuilder.command(command);
        processBuilder.directory(file.getParent().toFile()); // Set the working directory to the parent directory of the file
        Process process = processBuilder.start();
    }

    private static List<String> getCommand(Path file, Path sevenZipPath) {
        String inputFileName = file.getFileName().toString();
        // Output filename is equal to the inputFilename without any extension
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
        // Command as a list of strings
        List<String> command = Arrays.asList(sevenZipPath.toString(), "a", "-t7z", "-mx=9", "-m0=lzma2", "-md=64m", "-mfb=273", "-ms=on", outputFileName + ".7z", inputFileName);
        return command;
    }


    public static void deleteExtractedFiles(List<Path> workingFiles) {
        for (Path file : workingFiles) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                System.out.println("Error deleting file: " + file.getFileName());
            }
        }
    }


}
