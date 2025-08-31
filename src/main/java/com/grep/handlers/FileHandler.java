package com.grep.handlers;

import com.grep.Grep;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler implements InputHandler {
    @Override
    public void handle(String[] args) {
        Grep grep = new Grep(args[1]);

        String fileName = args[2];

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line = bufferedReader.readLine();

            int exitCode = 1;
            while (line != null) {
                if (grep.match(line)) {
                    exitCode = 0;
                    System.out.println(line);
                }

                line = bufferedReader.readLine();
            }

            System.exit(exitCode);
        } catch (FileNotFoundException e) {
            System.out.println(fileName + ": No such file or directory");
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
