package orderbook.domain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_stock")
public class CustomerStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private Integer amount;

    public CustomerStock() {
    }

    public CustomerStock(Customer customer, Asset asset, Integer amount) {
        this.customer = customer;
        this.asset = asset;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
