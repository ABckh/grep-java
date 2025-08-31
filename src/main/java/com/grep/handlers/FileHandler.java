package com.grep.handlers;

import com.grep.Grep;

public class FileHandler implements InputHandler {
    private static final int FIRST_FILE_INDEX = 2;
    private final FileProcessor fileProcessor = new FileProcessor();

    @Override
    public int handle(String[] args) {
        Grep grep = new Grep(args[1]);

        int exitCode = 1;
        boolean showFileName = FIRST_FILE_INDEX + 1 != args.length;

        for (int i = FIRST_FILE_INDEX; i < args.length; i++) {
            String fileName = args[i];
            exitCode = exitCode == 1
                    ? fileProcessor.process(fileName, grep, showFileName)
                    : 0;
        }

        return exitCode;
    }
}
