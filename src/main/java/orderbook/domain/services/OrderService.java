package orderbook.domain.services;

import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Book;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Order;

import orderbook.exceptions.ExceptionOrder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static orderbook.enuns.OrderStatus.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllOrders(){
            return orderRepository.findAll();
    }

    public List<Order> findByOpenOrders(){
        return orderRepository.findByOpenOrders();
    }

    public Order findOrderById(Long id){
        return orderRepository.findById(id).orElseThrow();
    }

    public Order createNewOrder(OrderRequest orderRequest){
        Order order = new Order(
                new Customer(orderRequest.getCustomerId()),
                orderRequest.getOrderType(),
                orderRequest.getPrice(),
                orderRequest.getAmount(),
                PENDING,
                LocalDateTime.now(),
                new Book(orderRequest.getBookId())
        );

        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, OrderRequest orderRequest){
        Order order = findOrderById(id);
        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());
        order.setLocalDateTime(LocalDateTime.now());

        if (order.getOrderStatus() != PENDING) {
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        return orderRepository.save(order);
    }

    public void cancelOrder(Long id){
        Order order = findOrderById(id);
        if(order.getOrderStatus() != PENDING){
            throw new ExceptionOrder("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        order.setOrderStatus(CANCELED);
        order.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(order);
    }

}
