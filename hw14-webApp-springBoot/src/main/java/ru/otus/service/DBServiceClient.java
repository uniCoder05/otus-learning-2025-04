package ru.otus.service;

import java.util.List;
import ru.otus.dto.ClientDTO;

public interface DBServiceClient {

    ClientDTO saveClient(ClientDTO clientDTO);

    List<ClientDTO> findAll();
}
