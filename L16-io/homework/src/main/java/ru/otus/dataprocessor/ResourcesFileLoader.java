package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() throws IOException {
        // читает файл, парсит и возвращает результат
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileProcessException("Файл не найден: " + fileName);
            }
            return mapper.readValue(is, new TypeReference<>() {});
        }
    }
}
