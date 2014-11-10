package com.epam.ptl.reader;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CsvDataFileReader implements DataFileReader {

    private static final Logger LOG = Logger.getLogger(CsvDataFileReader.class.getName());

    private String fileName;
    private int index;
    private CsvPreference preference = CsvPreference.STANDARD_PREFERENCE;

    public CsvDataFileReader(String fileName, int index) {
        this.fileName = fileName;
        this.index = index;
    }

    public CsvDataFileReader(String fileName, int index, CsvPreference preference) {
        this.fileName = fileName;
        this.preference = preference;
        this.index = index;
    }

    @Override
    public Map<String, List<String>> load() {
        ICsvListReader reader = null;
        Map<String, List<String>> result = new HashMap<>();
        try {
            reader = new CsvListReader(new FileReader(fileName), preference);
            List<String> line;
            while( ( line = reader.read() ) != null ) {
                result.put(line.get(index), line);
            }
        } catch (IOException e) {
            throw new DataFileReaderException("Error loading file " + fileName, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.warning("Could not close file " + fileName);
                }
            }
        }
        return result;
    }

}
