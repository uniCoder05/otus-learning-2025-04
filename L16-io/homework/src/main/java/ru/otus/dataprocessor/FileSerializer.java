package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) throws IOException {
        // формирует результирующий json и сохраняет его в файл
        var file = new File(fileName);
        mapper.writeValue(file, data);
    }
}
