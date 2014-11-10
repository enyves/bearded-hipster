package com.epam.ptl.reader;

public class DataFileReaderFactory {

    public DataFileReader getReader(String fileName, int indexColumn) {
        return new CsvDataFileReader(fileName, indexColumn);
    }
}
