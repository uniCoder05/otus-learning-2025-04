package ru.otus.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.ClientDTO;
import ru.otus.service.DBServiceClient;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ClientApiController {

    private final DBServiceClient dbServiceClient;

    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return dbServiceClient.findAll();
    }

    @PostMapping("/clients")
    public ClientDTO createClient(@RequestBody ClientDTO clientDTO) {
        return dbServiceClient.saveClient(clientDTO);
    }
}
