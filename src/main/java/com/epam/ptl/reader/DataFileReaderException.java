package com.epam.ptl.reader;

public class DataFileReaderException extends RuntimeException {

    public DataFileReaderException(String message) {
        super(message);
    }

    public DataFileReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
