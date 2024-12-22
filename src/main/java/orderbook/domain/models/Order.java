package orderbook.domain.models;

import jakarta.persistence.*;
import orderbook.enuns.OrderStatus;
import orderbook.enuns.OrderType;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordens")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Double price;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public Order() {}

    public Order(
                 Customer customer,
                 OrderType orderType,
                 Double price,
                 Integer amount,
                 OrderStatus orderStatus,
                 LocalDateTime localDateTime,
                 Book book) {

        this.customer = customer;
        this.orderType = orderType;
        this.price = price;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.localDateTime = localDateTime;
        this.book = book;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
