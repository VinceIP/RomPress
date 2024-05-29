package org.rompress;

import org.rompress.utils.CompressionUtils;
import org.rompress.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class PressCommand {
    Path workingPath;
    List<Path> workingFiles;
    String outputDir;
    long fileCount;
    //In bytes;
    long directorySizeBefore;
    long directorySizeAfter;
    long directorySizeDifference;

    public void execute(List<String> argsList) throws InvalidArgumentException {
        //Get a valid input directory from user's args
        String inputDir = getInputDirectory(argsList);
        if (Objects.equals(inputDir, "")) {
            throw InvalidArgumentException.missingInput();
        }

        try {
            //Scan files to be processed and display their current size in bytes
            workingFiles = CompressionUtils.scanArchives(inputDir);
            workingPath = Paths.get(inputDir);
            directorySizeBefore = CompressionUtils.calculateDirectorySize(workingPath);
            System.out.println("Found " + workingFiles.size() + " files to process totalling " + directorySizeBefore + " bytes.");
            //Extract files
            CompressionUtils.extractFiles(workingPath, workingFiles);
            //Scan output folder of ROMs
            workingFiles = CompressionUtils.scanRoms(outputDir);
            System.out.println("Found: " + workingFiles.size() + " ROM files totalling " + CompressionUtils.calculateDirectorySize(CompressionUtils.outputPath) + " bytes.\n" +
                    "Compressing to 7zip.");
            //Recompress scanned files to 7z
            //CompressionUtils.compressFilesAs7z(workingFiles);
            System.out.println("Finished.");
            //Cleanup previously extracted roms
            CompressionUtils.deleteExtractedFiles(workingFiles);
            workingPath = workingPath.resolve("output");
            //Calculate size of directory after compression operation
            directorySizeAfter = CompressionUtils.calculateDirectorySize(workingPath);
            directorySizeDifference = directorySizeBefore-directorySizeAfter;
            System.out.println("Directory size before compression: " + FileUtils.humanReadableByteCount(directorySizeBefore));
            System.out.println("Directory size after compression: " + FileUtils.humanReadableByteCount(directorySizeAfter));
            System.out.println("Disk space saved in operation: " + FileUtils.humanReadableByteCount(directorySizeDifference));



        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String getInputDirectory(List<String> argsList) {
        int inputIndex = argsList.indexOf("--input");
        if (inputIndex != -1 && inputIndex + 1 < argsList.size()) {
            return argsList.get(inputIndex + 1);
        }
        return "";
    }


}
