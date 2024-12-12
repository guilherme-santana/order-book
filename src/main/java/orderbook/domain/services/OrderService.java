package orderbook.domain.services;

import orderbook.dataprovider.repositories.OrderRepository;
import orderbook.domain.models.Book;
import orderbook.domain.models.Customer;
import orderbook.domain.models.Order;

import orderbook.enuns.OrderStatus;
import orderbook.exceptions.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static orderbook.enuns.OrderStatus.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;

    @Autowired
    public OrderService(OrderRepository orderRepository, BookService bookService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
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

    public OrderResponse createNewOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setBook(new Book(orderRequest.getBookId()));
        order.setCustomer(new Customer(orderRequest.getCustomerId()));
        order.setOrderType(orderRequest.getOrderType());
        order.setPrice(orderRequest.getPrice());
        order.setAmount(orderRequest.getAmount());
        order.setLocalDateTime(LocalDateTime.now());
        order.setOrderStatus(PENDING);

        Order orderCreated = orderRepository.save(order);

        OrderResponse response = new OrderResponse();

        response.setId(orderCreated.getId());
        response.setOrderType(orderCreated.getOrderType());
        response.setPrice(orderCreated.getPrice());
        response.setAmount(orderCreated.getAmount());
        response.setOrderStatus(PENDING);
        response.setLocalDateTime(orderCreated.getLocalDateTime());
        Book bookName = bookService.findBookById(order.getBook().getId());
        response.setBookName(bookName.getName());

        return response;
    }

    public OrderResponse updateOrder(Long id, OrderRequest orderRequest){
        Order order = findOrderById(id);
        order.setAmount(orderRequest.getAmount());
        order.setPrice(orderRequest.getPrice());
        order.setLocalDateTime(LocalDateTime.now());

        if (order.getOrderStatus() != PENDING) {
            throw new ExceptionHandler("Ordens executadas ou canceladas não podem ser alteradas!");
        }
        Order orderCreated = orderRepository.save(order);
        OrderResponse response = new OrderResponse();
        response.setId(orderCreated.getId());
        response.setOrderType(orderCreated.getOrderType());
        response.setPrice(orderCreated.getPrice());
        response.setAmount(orderCreated.getAmount());
        response.setOrderStatus(orderCreated.getOrderStatus());
        response.setLocalDateTime(orderCreated.getLocalDateTime());
        Book bookName = bookService.findBookById(order.getBook().getId());
        response.setBookName(bookName.getName());

        return response;
    }

    public void cancelOrder(Long id){
        Order order = findOrderById(id);
        if(order.getOrderStatus() != PENDING){
            throw new ExceptionHandler("Ordens executadas ou canceladas não podem ser alteradas!");
        }

        order.setOrderStatus(CANCELED);
        order.setLocalDateTime(LocalDateTime.now());
        orderRepository.save(order);
    }

}
