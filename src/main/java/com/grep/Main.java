package com.grep;

import com.grep.handlers.DirectoryHandler;
import com.grep.handlers.FileHandler;
import com.grep.handlers.InputHandler;
import com.grep.handlers.StdOutHandler;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2 && !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        InputHandler handler = getInputHandler(args);
        System.exit(handler.handle(args));
    }

    private static InputHandler getInputHandler(String[] args) {
        if (args.length == 2) {
            return new StdOutHandler();
        }
        if (args[0].equals("-r")) {
            return new DirectoryHandler();
        }
        return new FileHandler();
    }
}
