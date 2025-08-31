package ru.otus.controller;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.dto.ClientDTO;
import ru.otus.service.DBServiceClient;

@Controller
@AllArgsConstructor
public class ClientController {

    private final DBServiceClient dbServiceClient;

    @GetMapping("/clients")
    public String getClientsPage(Model model) {
        List<ClientDTO> clientsDTO = dbServiceClient.findAll();
        model.addAttribute("clients", clientsDTO);

        return "clients";
    }
}
