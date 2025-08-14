package ru.otus.crm.controller;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.dto.ClientDTO;
import ru.otus.crm.dto.ClientMapper;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

public class ClientController {

    private final DBServiceClient dbServiceClient;
    private final ClientMapper clientMapper;

    public ClientController(DBServiceClient dbServiceClient, ClientMapper clientMapper) {
        this.dbServiceClient = dbServiceClient;
        this.clientMapper = clientMapper;
    }

    public List<ClientDTO> getAllClients() {
        return dbServiceClient.findAll().stream().map(clientMapper::toDTO).toList();
    }

    public ClientDTO create(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client savedClient = dbServiceClient.saveClient(client);

        return clientMapper.toDTO(savedClient);
    }

    public Optional<ClientDTO> getById(long id) {
        Client client = dbServiceClient.getClient(id).orElse(null);

        return Optional.ofNullable(client).map(clientMapper::toDTO);
    }
}
