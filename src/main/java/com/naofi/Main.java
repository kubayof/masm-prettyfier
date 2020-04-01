package com.naofi;

import java.io.*;
import java.util.Scanner;

public class Main {
    String input;
    String output;
    int[] commandCounts;
    int beforeCommentCount;
    String pattern = "[.\\w\\d_,\\]\\[:+\\-*()''?$@ ]+";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: asmpf [input file name] [output file name]");
            System.exit(0);
        }
        new Main(args[0], args[1]);
    }

    private Main(String input, String output) throws IOException {
        this.input = input;
        this.output = output;
        commandCounts = new int[] {10, 16, 20};
        beforeCommentCount = 36;

        run();
    }

    private void run() throws IOException {
        try (Scanner scan = new Scanner(new FileInputStream(input));
             PrintWriter writer = new PrintWriter(new FileOutputStream(output))) {

            scan.useDelimiter("\n");
            String line;
            String lowerLine;
            String[] lines;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                lines = line.toLowerCase().split(";");
                if (lines.length < 1) {
                    continue;
                }

                lowerLine = lines[0];

                if (lowerLine.contains("end") ||
                        lowerLine.contains("proc") ||
                        lowerLine.contains("segment") ||
                        lowerLine.contains("ends") ||
                        lowerLine.contains("endp") ||
                        lowerLine.contains(".") ||
                        (lines[0].split(":[\\t\\n ]*").length == 1 && lowerLine.contains(":"))) {
                    writer.println(defLine(line));
                } else {
                    writer.println(commandLine(line));
                }
            }
        }
    }

    private String commandLine(String line) {
        return "\t" + defLine(line);
    }

    private String defLine(String line) {
        StringBuilder result = new StringBuilder();
        String[] parts = line.split(";");
        String currentLine = null;
        if (parts.length > 0) {
            Scanner scan = new Scanner(parts[0]);
            scan.useDelimiter("[ \\t]+");
            if (scan.hasNext(pattern)) {
                currentLine = scan.next(pattern);
                result.append(currentLine);
                while (scan.hasNext(pattern)) {
                    result.append(" ".repeat(getCommandDelimLen(currentLine.length())));
                    currentLine = scan.next(pattern);
                    result.append(currentLine);
                }
            }
            if (currentLine != null) {
                if (parts.length > 1) {
                    result.append(" ".repeat(beforeCommentCount - currentLine.length()));
                }
            }

            if (parts.length > 1) {
                for (int i = 1; i < parts.length; i++) {
                    result.append(";");
                    result.append(parts[i]);
                }
            }
        }

        return result.toString();
    }

    private int getCommandDelimLen(int curLen) {
        int i = 0;
        while (commandCounts[i] < curLen) i++;

        return curLen;
    }
}
