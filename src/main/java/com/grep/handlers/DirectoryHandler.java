package com.grep.handlers;

import com.grep.Grep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryHandler implements InputHandler {
    private final FileProcessor fileProcessor = new FileProcessor();

    @Override
    public int handle(String[] args) {
        Grep grep = new Grep(args[2]);
        String dir = args[3];

        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            boolean matched = stream
                    .filter(path -> !Files.isDirectory(path))
                    .map(fileName -> fileProcessor.process(fileName.toString(), grep, true))
                    .collect(Collectors.toSet())
                    .contains(0);

            return matched ? 0 : 1;
        } catch (IOException e) {
            System.out.println("I/O Error");
        }

        return 1;
    }
}
