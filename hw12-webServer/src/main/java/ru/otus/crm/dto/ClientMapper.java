package ru.otus.crm.dto;

import java.util.List;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

public class ClientMapper implements Mapper<ClientDTO, Client> {

    @Override
    public ClientDTO toDTO(Client client) {
        Long id = client.getId();
        String name = client.getName();
        String address = client.getAddress().getStreet();
        List<String> phones = client.getPhones().stream().map(Phone::getNumber).toList();

        return new ClientDTO(id, name, address, phones);
    }

    @Override
    public Client toEntity(ClientDTO clientDTO) {
        Long id = clientDTO.id();
        String name = clientDTO.name();
        Address address = new Address(clientDTO.address());
        List<Phone> phones = clientDTO.phones().stream().map(Phone::new).toList();

        return new Client(id, name, address, phones);
    }
}
