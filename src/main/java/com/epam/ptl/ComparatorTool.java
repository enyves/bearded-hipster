package com.epam.ptl;

import
import com.epam.ptl.reader.CsvDataFileReader;
import com.epam.ptl.reader.DataFileReader;
import com.epam.ptl.reader.DataFileReaderFactory;

import java.io.*;
import java.util.List;
import java.util.Map;


public class ComparatorTool {

    public static final String PREFIX_1 = "1_";
    public static final String PREFIX_2 = "2_";
    public static final String EXTENSION_INTERSECT = ".intersect";
    public static final String EXTENSION_COMPLEMENT = ".complement";
    public static final DataFileReaderFactory factory = new DataFileReaderFactory();


    public static void main(String[] args) {
        int columnIndex = 0;

        if (args.length < 2) {
            printUsage();

        } else

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

            Map<String, List<String>> data1 = readDataFromFile(fileName1, columnIndex);
            Map<String, List<String>> data2 = readDataFromFile(fileName2, columnIndex);

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



    private static Map<String, List<String>> readDataFromFile(String fileName, int indexColumn) {
        DataFileReader reader = factory.getReader(fileName, indexColumn);
        return reader.load();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("comparator file1 file2");
    }
}
