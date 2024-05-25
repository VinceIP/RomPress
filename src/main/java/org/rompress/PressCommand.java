package org.rompress;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class PressCommand {
    Path workingPath;
    List<Path> workingFiles;
    long fileCount;

    public void execute(List<String> argsList) throws InvalidArgumentException {
        String inputDir = getInputDirectory(argsList);
        if (Objects.equals(inputDir, "")) {
            throw InvalidArgumentException.missingInput();
        }

        try {
            //Count files to be processed
            countAndStoreFiles(inputDir);
            System.out.println("Found " + fileCount + " files to process.");
            //Create output directory in the working directory
            extractFiles(workingPath);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private String getInputDirectory(List<String> argsList) {
        int inputIndex = argsList.indexOf("--input");
        if (inputIndex != -1 && inputIndex + 1 < argsList.size()) {
            return argsList.get(inputIndex + 1);
        }
        return "";
    }

    private void countAndStoreFiles(String inputDir) throws IOException, InvalidArgumentException {
        workingPath = Paths.get(inputDir);
        if (!Files.exists(workingPath) || !Files.isDirectory(workingPath)) {
            throw new InvalidArgumentException("ERROR: The directory '" + inputDir + "' is invalid or does not exist.");
        }
        System.out.println("Pressing directory: " + "'" + inputDir + "'");
        try (Stream<Path> fileStream = Files.list(workingPath)) {
            workingFiles = fileStream.filter(
                    p -> {
                        String fileName = p.getFileName().toString();
                        return fileName.endsWith(".zip") || fileName.endsWith(".rar");
                    }).collect(Collectors.toList());
            fileCount = workingFiles.size();

        }
    }

    private void extractFiles(Path path) throws IOException {
        //Create an output directory for extracted files if it doesn't exist
        String outputDir = "output";
        Path outputPath = path.resolve(outputDir);
        if(!Files.exists(outputPath)) Files.createDirectory(outputPath);
        //For each file in our filtered list of files
        for(Path file: workingFiles){
            System.out.println("Extracting file: " + file.toString());
            extractFile(file, outputPath);
        }

    }

    private void extractFile(Path zipFilePath, Path outputPath) throws IOException {
        //Try to open the zip file and enumerate its contents
        try(ZipFile zipFile = new ZipFile(zipFilePath.toFile())){
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                //Set a path for this entry by resolving it to outputPath - "output/entry.nes"
                Path entryOutputPath = outputPath.resolve(entry.getName());

                //If this entry happens to be a subdirectory in the zip file, create a directory to store it on disk
                if(entry.isDirectory()){
                    Files.createDirectories(entryOutputPath);
                } else {
                    //Make sure the output has a parent directory
                    if(entryOutputPath.getParent()  != null){
                        Files.createDirectories(entryOutputPath.getParent());
                    }
                    //Extract the zip
                    try(InputStream in = zipFile.getInputStream(entry);
                        OutputStream out = Files.newOutputStream(entryOutputPath)){
                        in.transferTo(out);
                    }
                }
            }

        }
    }
}
