package org.rompress;

public class InvalidArgumentException extends Exception{
    final static String USAGE_MESSAGE = "RomPress version: " + RomPress.VERSION + "\n" +
            "Example usage:\n" +
            "--help: Display help.\n" +
            "--press: Intelligently compress a directory of files to save disk space. Input directory is needed using --input ''directory''\n" +
            "--input: Provide RomPress with an input directory to work on.";

    public InvalidArgumentException(String message){
        super(message);
    }

    public static InvalidArgumentException missingInput(){
        return new InvalidArgumentException("ERROR: Missing or invalid input directory.\n" + USAGE_MESSAGE);
    }

    public static InvalidArgumentException invalidCommand(){
        return new InvalidArgumentException("ERROR: Invalid command provided.\n" + USAGE_MESSAGE);
    }

    public static InvalidArgumentException missingCommand(){
        return new InvalidArgumentException(USAGE_MESSAGE);
    }

}
