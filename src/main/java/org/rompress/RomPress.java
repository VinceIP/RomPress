package org.rompress;

public class RomPress {
    public static final String VERSION = "0.1";
    public static void main(String[] args) {
        CommandHandler handler = new CommandHandler();
        try {
            handler.handle(args);
        } catch (InvalidArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
