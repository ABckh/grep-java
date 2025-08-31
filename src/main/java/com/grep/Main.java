package com.grep;

import com.grep.handlers.FileHandler;
import com.grep.handlers.StdOutHandler;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }
        if (args.length > 3) {
            System.out.println("Usage: ./your_program.sh -E <pattern> <file>");
            System.exit(1);
        }

        FileHandler fileHandler = new FileHandler();
        StdOutHandler stdOutHandler = new StdOutHandler();

        if (args.length == 3) {
            fileHandler.handle(args);
        } else {
            stdOutHandler.handle(args);
        }
    }
}
