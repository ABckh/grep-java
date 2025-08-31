package com.grep.handlers;

import com.grep.Grep;

import java.util.Scanner;

public class StdOutHandler implements InputHandler {
    @Override
    public int handle(String[] args) {
        Grep grep = new Grep(args[1]);

        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();

        if (grep.match(inputLine)) {
            System.out.println("Success");
            return 0;
        }

        return 1;
    }
}
