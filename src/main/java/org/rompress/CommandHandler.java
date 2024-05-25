package org.rompress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {
    public void handle(String[] args) throws InvalidArgumentException {
        List<String> argsList;
        //Parse arguments and pass to appropriate command
        if (args.length >= 1) {
            argsList = new ArrayList<>(Arrays.asList(args));
            if (argsList.contains("--press")) {
                new PressCommand().execute(argsList);
            } else throw InvalidArgumentException.invalidCommand();
        } else throw InvalidArgumentException.missingCommand();


    }
}