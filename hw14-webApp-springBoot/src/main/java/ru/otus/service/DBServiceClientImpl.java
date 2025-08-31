package ru.otus.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.dto.ClientDTO;
import ru.otus.dto.ClientMapper;
import ru.otus.repository.ClientRepository;
import ru.otus.sessionmanager.TransactionClient;

@Service
@RequiredArgsConstructor
public class DBServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DBServiceClientImpl.class);

    private final TransactionClient transactionClient;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientDTO saveClient(ClientDTO clientDTO) {
        var entity = clientMapper.toEntity(clientDTO);
        var savedEntity = transactionClient.doInTransaction(() -> {
            var savedClient = clientRepository.save(entity);
            log.info("saved client: {}", savedClient);
            return savedClient;
        });
        return clientMapper.toDTO(savedEntity);
    }

    @Override
    public List<ClientDTO> findAll() {
        var clients = new ArrayList<>(clientRepository.findAll())
                .stream().map(clientMapper::toDTO).toList();
        log.info("clientList:{}", clients);
        return clients;
    }
}
