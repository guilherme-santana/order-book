package orderbook.domain.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private Integer document;
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}
