package ru.otus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.dto.ClientDTO;
import ru.otus.repository.ClientRepository;
import ru.otus.service.DBServiceClient;

@Component("prepDemo")
@AllArgsConstructor
public class PrepDemo implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PrepDemo.class);
    private static final Random random = new Random();
    private static final String STREETS_FILE = "streets.txt";

    private final DBServiceClient dbServiceClient;
    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) throws Exception {
        if (clientRepository.count() == 0) {
            generateData();
        }
    }

    private void generateData() throws IOException, URISyntaxException {
        log.info("------<<<<< Заполнение данными >>>>------");
        String[] streets = readTextResource(STREETS_FILE);
        int streetsAmount = streets.length;
        for (int i = 0; i < streetsAmount; i++) {
            ClientDTO clientDTO = new ClientDTO(null, "Client_" + (i + 1), streets[i], generatePhones());
            dbServiceClient.saveClient(clientDTO);
        }
        log.info("------<<<<< заполнение данными завершено >>>>------");
    }

    private List<String> generatePhones() {
        int amount = random.nextInt(3);
        List<String> phones = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            phones.add(generatePhoneNumber());
        }
        return phones;
    }

    private String generatePhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("8");
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    private String[] readTextResource(String filename) throws IOException, URISyntaxException {
        var uri = getClass().getResource(String.format("/%s", filename)).toURI();
        List<String> lines = Files.readAllLines(Paths.get(uri));
        return lines.toArray(new String[0]);
    }
}
