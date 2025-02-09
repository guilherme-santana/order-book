package orderbook.domain.models;

import jakarta.persistence.*;
import orderbook.dataprovider.exceptions.BusinessException;

import java.math.BigDecimal;

import static orderbook.domain.messages.Messages.SALDO_INSUFICIENTE;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal balance;

    public Wallet(BigDecimal balance, Customer customer) {
        this.balance = balance;
        this.customer = customer;
    }

    public Wallet() {
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

    public BigDecimal getBalance() {
        return balance;
    }

    private void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void updateBalance(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException(SALDO_INSUFICIENTE);
        }
        setBalance(value);
    }

}
