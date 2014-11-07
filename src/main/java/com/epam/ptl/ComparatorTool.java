package com.epam.ptl;

import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ComparatorTool {

    public static final String PREFIX_1 = "1_";
    public static final String PREFIX_2 = "2_";
    public static final String EXTENSION_INTERSECT = ".intersect";
    public static final String EXTENSION_COMPLEMENT = ".complement";


    public static void main(String[] args) {
        int columnIndex = 0;

        if (args.length < 2) {
            printUsage();
        }

        if (args.length > 2) {
            columnIndex = Integer.parseInt(args[2]);
        }

        try {
            compareFiles(args[0], args[1], columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void compareFiles(String fileName1, String fileName2, int columnIndex) throws IOException {
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);
        String fileIntersect1 = PREFIX_1 + file1.getName() + EXTENSION_INTERSECT;
        String fileIntersect2 = PREFIX_2 + file2.getName() + EXTENSION_INTERSECT;
        String fileComplement1 = PREFIX_1 + file1.getName() + EXTENSION_COMPLEMENT;
        String fileComplement2 = PREFIX_2 + file2.getName() + EXTENSION_COMPLEMENT;


        try (PrintWriter fileIntersectWriter1 = new PrintWriter(new File(fileIntersect1));
            PrintWriter fileIntersectWriter2 = new PrintWriter(new File(fileIntersect2));
            PrintWriter fileComplementWriter1 = new PrintWriter(new File(fileComplement1));
            PrintWriter fileComplementWriter2 = new PrintWriter(new File(fileComplement2))) {

            Map<String, String[]> data1 = readDataFromFile(fileName1, columnIndex);
            Map<String, String[]> data2 = readDataFromFile(fileName2, columnIndex);

            int complementerCount1 = 0;
            int complementerCount2 = 0;
            int intersectCount = 0;

            for (String key: data1.keySet()) {
                if (data2.containsKey(key)) {
                    fileIntersectWriter1.println(implode(data1.get(key), ","));
                    fileIntersectWriter2.println(implode(data2.get(key), ","));
                    intersectCount++;
                } else {
                    fileComplementWriter1.println(implode(data1.get(key), ","));
                    complementerCount1++;
                }
            }

            System.out.println(String.format("%d IDs found in both files, lines written to files %s and %s", intersectCount, fileIntersect1, fileIntersect2));
            System.out.println(String.format("%d IDs not found in second file, lines written to file %s", complementerCount1,  fileComplement1));

            for (String key: data2.keySet()) {
                if (!data1.containsKey(key)) {
                    fileComplementWriter2.println(implode(data2.get(key), ","));
                    complementerCount2++;
                }
            }

            System.out.println(String.format("%d IDs not found in first file, lines written to file %s", complementerCount2,  fileComplement2));

        }
    }

    private static String implode(String[] strings, String separator) {
        StringBuilder builder = new StringBuilder();
        if (strings.length > 0) {
            builder.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                builder.append(separator);
                if (strings[i].contains(separator)) {
                    builder.append("\"");
                }
                builder.append(strings[i]);
                if (strings[i].contains(separator)) {
                    builder.append("\"");
                }
            }
        }
        return builder.toString();
    }



    private static Map<String, String[]> readDataFromFile(String fileName, int columnIndex) throws IOException {
        Map<String, String[]> result = new HashMap<>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(fileName));
            String[] nextLine;
            int lineNr = 0;
            while ((nextLine = reader.readNext()) != null) {
                lineNr++;
                if (nextLine.length > columnIndex) {
                    if (result.containsKey(nextLine[columnIndex])) {
                        throw new IllegalArgumentException(String.format("File %s contains duplicate ID's!", fileName));
                    }
                    result.put(nextLine[columnIndex], nextLine);
                }
            }
            System.out.println(String.format("%d lines read from %s", lineNr, fileName));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("comparator file1 file2");
    }
}
