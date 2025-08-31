package com.grep.handlers;

import com.grep.Grep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler implements InputHandler {
    private static final int FIRST_FILE_INDEX = 2;

    @Override
    public int handle(String[] args) {
        Grep grep = new Grep(args[1]);

        int exitCode = 1;
        boolean showFileName = FIRST_FILE_INDEX + 1 != args.length;

        for (int i = FIRST_FILE_INDEX; i < args.length; i++) {
            String fileName = args[i];
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                boolean matched = processFile(grep, reader, fileName, showFileName);
                if (matched) {
                    exitCode = 0;
                }
            } catch (FileNotFoundException e) {
                System.out.println(fileName + ": No such file or directory");
            } catch (IOException e) {
                System.out.println(fileName + ": I/O Error");
            }
        }

        return exitCode;
    }

    private boolean processFile(Grep grep, BufferedReader bufferedReader, String fileName, boolean showFileName) throws IOException {
        String line = bufferedReader.readLine();

        boolean matched = false;
        while (line != null) {
            if (grep.match(line)) {
                matched = true;
                printMatched(fileName, line, showFileName);
            }

            line = bufferedReader.readLine();
        }

        return matched;
    }

    private void printMatched(String fileName, String line, boolean showFileName) {
        if (showFileName) {
            System.out.println(fileName + ":" + line);
        } else {
            System.out.println(line);
        }
    }
}
