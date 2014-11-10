package com.epam.ptl.reader;

import java.util.List;
import java.util.Map;

public interface DataFileReader {
    Map<String, List<String>> load();
}
