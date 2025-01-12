package orderbook.domain.models;

import jakarta.persistence.*;
import orderbook.enuns.OrderStatus;
import orderbook.enuns.OrderType;

import java.math.BigDecimal;
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

    @Column(columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal price;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;

    public Order() {}

    public Order(
                 Customer customer,
                 OrderType orderType,
                 BigDecimal price,
                 Integer amount,
                 OrderStatus orderStatus,
                 LocalDateTime localDateTime,
                 Asset asset) {
        this.customer = customer;
        this.orderType = orderType;
        this.price = price;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.localDateTime = localDateTime;
        this.asset = asset;
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

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getOriginalprice(Order order){
        return  order.getPrice().multiply(BigDecimal.valueOf(order.getAmount()));
    }

    public void setPrice(BigDecimal price) {
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

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

}
