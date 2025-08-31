package ru.otus.dto;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.otus.entity.Address;
import ru.otus.entity.Client;
import ru.otus.entity.Phone;

@Component
public class ClientMapper implements Mapper<ClientDTO, Client> {

    @Override
    public ClientDTO toDTO(Client client) {
        Long id = client.id();
        String name = client.name();
        String address = client.address() != null ? client.address().street() : null;
        List<String> phones = client.phones() != null
                ? client.phones().stream().map(Phone::number).toList()
                : Collections.emptyList();

        return new ClientDTO(id, name, address, phones);
    }

    @Override
    public Client toEntity(ClientDTO clientDTO) {
        Long id = clientDTO.id();
        String name = clientDTO.name();
        Address address = new Address(null, clientDTO.address());
        List<Phone> phones = clientDTO.phones().stream()
                .map(number -> new Phone(null, number))
                .toList();

        return new Client(id, name, address, phones);
    }
}
