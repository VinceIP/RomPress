package org.rompress;

import java.util.List;

public class PressCommand {
    public void execute(List<String> args){
        System.out.println("Press activated.");
        if(args.contains("--input")){
            System.out.println("Input provided. Thanks.");
        }
        else{
            System.out.println("Error. No input directory provided.");
        }
    }
}
