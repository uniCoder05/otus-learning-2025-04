package ru.otus.crm.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "client")
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("this-escape")
    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = (phones != null) ? new ArrayList<>(phones) : new ArrayList<>();
        if (this.address != null) {
            this.address.setClient(this);
        }
        this.phones.forEach(phone -> phone.setClient(this)); // обратная ссылка для каждого телефона
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address clonedAddress = (this.address != null) ? this.address.clone() : null;
        List<Phone> clonedPhones = new ArrayList<>();
        if (this.phones != null) {
            for (Phone phone : this.phones) {
                clonedPhones.add(phone.clone());
            }
        }
        return new Client(this.id, this.name, clonedAddress, clonedPhones);
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + ", address=" + address + ", phones=" + phones + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id)
                && Objects.equals(name, client.name)
                && Objects.equals(address, client.address)
                && Objects.equals(phones, client.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phones);
    }
}
